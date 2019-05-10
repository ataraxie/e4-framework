package de.scandio.e4.client.orchestration;

import de.scandio.e4.client.WorkerRestUtil;
import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.client.orchestration.phases.OrchestrationPhase;
import de.scandio.e4.client.orchestration.phases.PreparationPhase;
import de.scandio.e4.client.orchestration.phases.RunPhase;
import de.scandio.e4.dto.TestsStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;

public class OrchestrationUtil {

	public static void executePhases(ClientConfig clientConfig) throws Exception {

		// TODO upload plugins phase
		//uploadPlugins(clientConfig);

		final OrchestrationPhase preparationPhase = new PreparationPhase(clientConfig);
		preparationPhase.executePhase();

		final OrchestrationPhase runPhase = new RunPhase(clientConfig);
		runPhase.executePhase();


		System.out.println("Waiting until tests finish...");
		for (String workerURL : clientConfig.getWorkers()) {
			WorkerRestUtil.pollStatusUntil(workerURL, 5000, 1000, workerStatusResponse -> {
				if (workerStatusResponse.getTestsStatus().equals(TestsStatus.ERROR)) {
					throw new IllegalStateException("Worker "+workerURL+" failed tests!");
				}
				System.out.println("Worker "+workerURL+" tests status: " + workerStatusResponse.getTestsStatus());
				return workerStatusResponse.getTestsStatus().equals(TestsStatus.FINISHED);
			});
		}


		System.out.println("Tests are done!");
		System.out.println("SO. ENJOYABLE.");
		System.out.println("We are done - we just need to analyze the data now.");

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

}
