package de.scandio.e4.client.orchestration.phases;

import de.scandio.e4.client.WorkerRestUtil;
import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.dto.TestsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class RunPhase implements OrchestrationPhase {

	private Logger log = LoggerFactory.getLogger(RunPhase.class);

	private final ClientConfig clientConfig;

	public RunPhase(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	@Override
	public void executePhase() throws Exception {
		final List<String> workers = clientConfig.getWorkers();

		log.info("\n[PHASE - RUN]\n");

		for (String workerURL : workers) {
			log.info("Telling "+workerURL+" to start running tests.");
			final ResponseEntity<String> response = WorkerRestUtil.getStart(workerURL);
			if (response.getStatusCodeValue() != 200) {
				throw new Exception("Worker "+workerURL+" responded with "+response.getStatusCodeValue()+" for /e4/start.");
			}
		}

		log.info("All workers now know that they should run the tests. Checking for TestsStatus.");

		for (String workerURL : workers) {
			log.info("Checking TestsStatus of "+workerURL+" ...");
			WorkerRestUtil.pollStatusUntil(workerURL, 2000, 30, workerStatusResponse -> {
				if (workerStatusResponse.getTestsStatus().equals(TestsStatus.ERROR)) {
					throw new IllegalStateException("Worker "+workerURL+" failed to start tests!");
				}
				log.info("Worker "+workerURL+" tests status: " + workerStatusResponse.getTestsStatus());
				return workerStatusResponse.getTestsStatus().equals(TestsStatus.RUNNING);
			});
		}

		log.info("All workers are running the tests now!");
	}
}
