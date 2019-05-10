package de.scandio.e4.dto;

import java.util.Map;

public class ApplicationStatusResponse {
	private Map<String, Object> config;
	private PreparationStatus preparationStatus;

	public ApplicationStatusResponse() {}

	public ApplicationStatusResponse(Map<String, Object> config, PreparationStatus preparationStatus) {
		this.config = config;
		this.preparationStatus = preparationStatus;
	}

	public Map<String, Object> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Object> config) {
		this.config = config;
	}

	public PreparationStatus getPreparationStatus() {
		return preparationStatus;
	}

	public void setPreparationStatus(PreparationStatus preparationStatus) {
		this.preparationStatus = preparationStatus;
	}
}
