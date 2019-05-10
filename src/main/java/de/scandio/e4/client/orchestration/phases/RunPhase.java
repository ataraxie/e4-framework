package de.scandio.e4.client.orchestration.phases;

import de.scandio.e4.client.WorkerRestUtil;
import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.dto.TestsStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class RunPhase implements OrchestrationPhase {
	private final ClientConfig clientConfig;

	public RunPhase(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	@Override
	public void executePhase() throws Exception {
		final List<String> workers = clientConfig.getWorkers();

		System.out.println("\n[PHASE - RUN]\n");

		for (String workerURL : workers) {
			System.out.println("Telling "+workerURL+" to start running tests.");
			final ResponseEntity<String> response = WorkerRestUtil.postStart(workerURL);
			if (response.getStatusCodeValue() != 200) {
				throw new Exception("Worker "+workerURL+" responded with "+response.getStatusCodeValue()+" for /e4/start.");
			}
		}

		System.out.println("All workers now know that they should run the tests. Checking for TestsStatus.");

		for (String workerURL : workers) {
			System.out.println("Checking TestsStatus of "+workerURL+" ...");
			WorkerRestUtil.pollStatusUntil(workerURL, 2000, 10, workerStatusResponse -> {
				if (workerStatusResponse.getTestsStatus().equals(TestsStatus.ERROR)) {
					throw new IllegalStateException("Worker "+workerURL+" failed to start tests!");
				}

				return workerStatusResponse.getTestsStatus().equals(TestsStatus.RUNNING);
			});
		}

		System.out.println("All workers are running the tests now!");
	}
}
