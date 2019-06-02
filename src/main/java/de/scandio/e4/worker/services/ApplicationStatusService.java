package de.scandio.e4.worker.services;

import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.ApplicationStatusResponse;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApplicationStatusService {
	@Value("${output.dir:outputs}")
	private String outputDir;

	private WorkerConfig config;
	private PreparationStatus preparationStatus = PreparationStatus.UNPREPARED;
	private TestsStatus testsStatus = TestsStatus.NOT_RUNNING;

	public ApplicationStatusResponse getApplicationStatus() {
		return new ApplicationStatusResponse(config, preparationStatus, testsStatus);
	}

	public String getOutputDir() {
		return outputDir;
	}

	public WorkerConfig getConfig() {
		return config;
	}

	public void setConfig(WorkerConfig config) {
		this.config = config;
	}

	public void setPreparationStatus(PreparationStatus preparationStatus) {
		this.preparationStatus = preparationStatus;
	}

	public PreparationStatus getPreparationStatus() {
		return preparationStatus;
	}

	public TestsStatus getTestsStatus() {
		return testsStatus;
	}

	public void setTestsStatus(TestsStatus testsStatus) {
		this.testsStatus = testsStatus;
	}
}
