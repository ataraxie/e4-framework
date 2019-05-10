package de.scandio.e4.dto;

import de.scandio.e4.client.config.WorkerConfig;

import java.util.Map;

public class ApplicationStatusResponse {
	private WorkerConfig config;
	private PreparationStatus preparationStatus;
	private TestsStatus testsStatus;

	public ApplicationStatusResponse() {}

	public ApplicationStatusResponse(WorkerConfig config,
									 PreparationStatus preparationStatus,
									 TestsStatus testsStatus) {
		this.config = config;
		this.preparationStatus = preparationStatus;
		this.testsStatus = testsStatus;
	}

	public WorkerConfig getConfig() {
		return config;
	}

	public void setConfig(WorkerConfig config) {
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
