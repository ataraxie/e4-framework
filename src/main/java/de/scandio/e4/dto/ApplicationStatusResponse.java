package de.scandio.e4.dto;

import java.util.Map;

public class ApplicationStatusResponse {
	private Map<String, Object> config;
	private PreparationStatus preparationStatus;
	private TestsStatus testsStatus;

	public ApplicationStatusResponse() {}

	public ApplicationStatusResponse(Map<String, Object> config,
									 PreparationStatus preparationStatus,
									 TestsStatus testsStatus) {
		this.config = config;
		this.preparationStatus = preparationStatus;
		this.testsStatus = testsStatus;
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

	public TestsStatus getTestsStatus() {
		return testsStatus;
	}

	public void setTestsStatus(TestsStatus testsStatus) {
		this.testsStatus = testsStatus;
	}
}
