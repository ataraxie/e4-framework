package de.scandio.e4.worker.services;

import de.scandio.e4.dto.ApplicationStatusResponse;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApplicationStatusService {
	@Value("${screenshots.dir:screenshots}")
	private String screenshotsDir;

	private Map<String, Object> config; // TODO: replace Map with WorkerConfig dto everywhere
	private PreparationStatus preparationStatus = PreparationStatus.UNPREPARED;
	private TestsStatus testsStatus = TestsStatus.NOT_RUNNING;

	public ApplicationStatusResponse getApplicationStatus() {
		return new ApplicationStatusResponse(config, preparationStatus, testsStatus);
	}

	public String getScreenshotsDir() {
		return screenshotsDir;
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

	public TestsStatus getTestsStatus() {
		return testsStatus;
	}

	public void setTestsStatus(TestsStatus testsStatus) {
		this.testsStatus = testsStatus;
	}
}
