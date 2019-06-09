package de.scandio.e4.client.orchestration.phases;

import de.scandio.e4.client.WorkerRestUtil;
import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.PreparationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class PreparationPhase implements OrchestrationPhase {

	private static final Logger log = LoggerFactory.getLogger(PreparationPhase.class);

	private final ClientConfig clientConfig;

	public PreparationPhase(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	@Override
	public void executePhase() throws Exception {
		final List<String> workers = clientConfig.getWorkers();
		final int usersPerWorker = clientConfig.getNumConcurrentUsers() / workers.size();

		log.info("\n[PHASE - PREPARE]\n");

		final WorkerConfig workerConfig = WorkerConfig.from(clientConfig);
		log.info("Distributing config to workers:");
		log.info(workerConfig.toString());

		for (int i = 0; i < workers.size(); i++) {
			String workerUrl = workers.get(i);
			log.info("Telling "+workerUrl+" to prepare.");
			final ResponseEntity<String> response = WorkerRestUtil.postPrepare(workerUrl, i, workerConfig);
			if (response.getStatusCodeValue() != 200) {
				throw new Exception("Worker " + workerUrl + " responded with " + response.getStatusCodeValue() + " for /e4/prepare.");
			}
		}

		for (String workerURL : workers) {
			log.info("Waiting for "+workerURL+" to finish preparing...");
			WorkerRestUtil.pollStatusUntil(workerURL, 2000, 100, response -> {
				final PreparationStatus preparationStatus = response.getPreparationStatus();

				if (preparationStatus.equals(PreparationStatus.ONGOING) || preparationStatus.equals(PreparationStatus.UNPREPARED)) {
					log.info("Worker "+workerURL+" is still preparing...");
					return false;
				}

				if (preparationStatus.equals(PreparationStatus.ERROR)) {
					throw new IllegalStateException("Worker "+workerURL+" errored while preparing!");
				}

				log.info("Worker "+workerURL+" has finished preparing!");
				return true;
			});
		}

		log.info("All workers have finished the preparation phase!");
	}
}
