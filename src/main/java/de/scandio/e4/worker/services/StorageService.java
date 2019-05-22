package de.scandio.e4.worker.services;

import de.scandio.e4.worker.interfaces.Scenario;
import de.scandio.e4.worker.interfaces.VirtualUser;
import de.scandio.e4.worker.util.WorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class StorageService {

	private static final Logger log = LoggerFactory.getLogger(StorageService.class);

	public void recordMeasurement(
			VirtualUser virtualUser,
			Scenario scenario,
			Thread virtualUserThread,
			long timeTaken) throws Exception {

		log.warn("[RECORD] {{}} | {{}} | {{}} | {{}}", timeTaken, WorkerUtils.getRuntimeName(),
				virtualUser.getClass().getSimpleName(), scenario.getClass().getSimpleName());
	}
}
