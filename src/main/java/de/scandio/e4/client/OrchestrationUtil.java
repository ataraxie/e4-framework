package de.scandio.e4.client;

import de.scandio.e4.client.config.ClientConfig;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrchestrationUtil {

	public static void orchestrateWorkers(ClientConfig clientConfig) throws Exception {
		// TODO: upload pluginsToUpload to target via rest

		preparationPhase(clientConfig);

		runPhase(clientConfig);

		// TODO: PHASE 3 - Gather & Analyze
		// after each phase check status of all workers
	}


	private static void preparationPhase(ClientConfig clientConfig) throws Exception {
		final List<String> workers = clientConfig.getWorkers();
		final int usersPerWorker = clientConfig.getConcurrentUsers() / workers.size();

		System.out.println("\n[PHASE - PREPARE]\n");

		final Map<String, Object> preparationParameters = new HashMap<String, Object>(){{
			put("target", clientConfig.getTarget().getUrl());
			put("username", clientConfig.getTarget().getAdminUser());
			put("password", clientConfig.getTarget().getAdminPassword());
			put("testPackage", clientConfig.getTestPackage());
			put("repeatTests", clientConfig.getDurationInSeconds() > 0);
			put("virtualUsers", usersPerWorker);
			put("screenshotDir", clientConfig.getScreenshotDir());
		}};

		System.out.println("Distributing config to workers:");
		System.out.println(preparationParameters);

		for (String workerURL : workers) {
			System.out.println("Telling "+workerURL+" to prepare.");
			final ResponseEntity<String> response = WorkerRestUtil.postPrepare(workerURL, preparationParameters);
			if (response.getStatusCodeValue() != 200) {
				throw new Exception("Worker "+workerURL+" responded with "+response.getStatusCodeValue()+" for /e4/prepare.");
			}
		}

		for (String workerURL : workers) {
			System.out.println("Waiting for "+workerURL+" to finish preparing...");
			WorkerRestUtil.pollStatusUntil(workerURL, 3000, 10, workerStatusResponse -> {
				final boolean arePreparationsFinished = workerStatusResponse.arePreparationsFinished();
				System.out.println("Preparations of worker "+workerURL+" are finished: "+arePreparationsFinished);
				return arePreparationsFinished;
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
				final boolean areTestsRunning = workerStatusResponse.areTestsRunning();
				System.out.println("Tests on "+workerURL+" are running: "+areTestsRunning);
				return areTestsRunning;
			});
		}

		System.out.println("All workers are running the tests now!");
	}
}
