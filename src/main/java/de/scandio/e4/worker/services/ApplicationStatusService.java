package de.scandio.e4.worker.services;

import de.scandio.e4.dto.ApplicationStatusResponse;
import de.scandio.e4.dto.PreparationStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApplicationStatusService {

	private Map<String, Object> config; // TODO: replace Map with WorkerConfig entity
	private PreparationStatus preparationStatus = PreparationStatus.UNPREPARED;

	public ApplicationStatusResponse getApplicationStatus() {
		//applicationStatus.put("areTestsRunning", testRunnerService.areTestsRunning());
		//applicationStatus.put("storedUsers", userCredentialsService.getAllUsers());
		return new ApplicationStatusResponse(config, preparationStatus);
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

	public void setPreparationStatus(PreparationStatus preparationStatus) {
		this.preparationStatus = preparationStatus;
	}

	public PreparationStatus getPreparationStatus() {
		return preparationStatus;
	}
}
