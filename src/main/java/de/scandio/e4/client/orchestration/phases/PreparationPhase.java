package de.scandio.e4.client.orchestration.phases;

import de.scandio.e4.client.WorkerRestUtil;
import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.dto.PreparationStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreparationPhase implements OrchestrationPhase {
	private final ClientConfig clientConfig;

	public PreparationPhase(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	@Override
	public void executePhase() throws Exception {
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
}
