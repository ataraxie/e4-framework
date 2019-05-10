package de.scandio.e4.client;

import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.dto.PreparationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrchestrationUtil {

	public static void orchestrateWorkers(ClientConfig clientConfig) throws Exception {
		// TODO: upload pluginsToUpload to target via rest

		//uploadPlugins(clientConfig);

		preparationPhase(clientConfig);

		runPhase(clientConfig);

		// TODO: PHASE 3 - Gather & Analyze
		// after each phase check status of all workers
	}

	private static void uploadPlugins(ClientConfig clientConfig) throws Exception {
		final RestTemplate restTemplate = new RestTemplate();
		final ClientConfig.TargetConfig target = clientConfig.getTarget();
		final ResponseEntity<String> response = restTemplate.getForEntity(target.getUrl(), String.class);
		if (response.getStatusCodeValue() != 200) {
			throw new Exception("Could not connect to target. Return code: " + response.getStatusCodeValue());
		}

		System.out.println("Successfully connected to target at "+target);

		final List<String> appsToInstall = clientConfig.getAppsToInstall();
		for (String appToInstall : appsToInstall) {
			File f = new File(appToInstall);
			if(f.exists() && !f.isDirectory()) {
				System.out.println("Checked "+appToInstall+" -> Exists.");
			} else {
				throw new Exception("Could not find plugin to install: "+appToInstall);
			}
		}

		// TODO: check if plugin is already installed
		// TODO: install plugins via UPM REST API
		System.out.println("TODO: If we need to install plugins we would do it now - still need to figure out UPM REST API.");
	}


	private static void preparationPhase(ClientConfig clientConfig) throws Exception {
		final List<String> workers = clientConfig.getWorkers();
		final int usersPerWorker = clientConfig.getConcurrentUsers() / workers.size();

		System.out.println("\n[PHASE - PREPARE]\n");

		final Map<String, Object> workerConfig = new HashMap<String, Object>(){{
			put("target", clientConfig.getTarget().getUrl());
			put("username", clientConfig.getTarget().getAdminUser());
			put("password", clientConfig.getTarget().getAdminPassword());
			put("testPackage", clientConfig.getTestPackage());
			put("repeatTests", clientConfig.getDurationInSeconds() > 0);
			put("virtualUsers", usersPerWorker);

			// TODO: only pass this to worker via CommandLine not workerConfig
			put("screenshotDir", clientConfig.getScreenshotDir());
		}};

		System.out.println("Distributing config to workers:");
		System.out.println(workerConfig);

		for (String workerURL : workers) {
			System.out.println("Telling "+workerURL+" to prepare.");
			final ResponseEntity<String> response = WorkerRestUtil.postPrepare(workerURL, workerConfig);
			if (response.getStatusCodeValue() != 200) {
				throw new Exception("Worker " + workerURL + " responded with " + response.getStatusCodeValue() + " for /e4/prepare.");
			}
		}

		for (String workerURL : workers) {
			System.out.println("Waiting for "+workerURL+" to finish preparing...");
			WorkerRestUtil.pollStatusUntil(workerURL, 2000, 10, response -> {
				final PreparationStatus preparationStatus = response.getPreparationStatus();

				if (preparationStatus.equals(PreparationStatus.ONGOING) || preparationStatus.equals(PreparationStatus.UNPREPARED)) {
					System.out.println("Worker "+workerURL+" is still preparing...");
					return false;
				}

				if (preparationStatus.equals(PreparationStatus.ERROR) ||
						(preparationStatus.equals(PreparationStatus.FINISHED) && !response.getConfig().equals(workerConfig))) {
					throw new IllegalStateException("Worker "+workerURL+" errored while preparing!");
				}

				System.out.println("Worker "+workerURL+" has finished preparing!");
				return true;
			});
		}

		System.out.println("All workers have finished the preparation phase!");
	}


	private static void runPhase(ClientConfig clientConfig) throws Exception {
		final List<String> workers = clientConfig.getWorkers();

		System.out.println("\n[PHASE - RUN]\n");
		final Map<String, Object> startParameters = new HashMap<String, Object>(){{
			put("targetUrl", clientConfig.getTarget().getUrl());
			put("testPackage", clientConfig.getTestPackage());
			put("screenshotDir", clientConfig.getScreenshotDir());
		}};

		for (String workerURL : workers) {
			System.out.println("Telling "+workerURL+" to run.");
			final ResponseEntity<String> response = WorkerRestUtil.postStart(workerURL, startParameters);
			if (response.getStatusCodeValue() != 200) {
				throw new Exception("Worker "+workerURL+" responded with "+response.getStatusCodeValue()+" for /e4/start.");
			}
		}

		for (String workerURL : workers) {
			System.out.println("Waiting for "+workerURL+" to start runs...");
			WorkerRestUtil.pollStatusUntil(workerURL, 3000, 10, workerStatusResponse -> {
				// TODO: check for TestStatus
				return false;
			});
		}

		System.out.println("All workers are running the tests now!");
	}
}
