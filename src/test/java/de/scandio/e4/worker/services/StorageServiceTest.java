package de.scandio.e4.worker.services;

import de.scandio.e4.dto.Measurement;
import de.scandio.e4.worker.util.WorkerUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.mockito.Mockito.*;

public class StorageServiceTest {

	private static final Logger log = LoggerFactory.getLogger(StorageServiceTest.class);

	private StorageService storageService;

	@Mock
	ApplicationStatusService applicationStatusServiceMock;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Before
	public void setup() throws Exception {
		when(applicationStatusServiceMock.getOutputDir()).thenReturn("/tmp/e4/out");
		this.storageService = new StorageService(applicationStatusServiceMock);
	}

	//@Test
	public void testRecordMeasurement() throws Exception {
		// Test will pass if there is no exception
		Measurement measurement = new Measurement(
				1000L,
				WorkerUtils.getRuntimeName(),
				"VirtualUser",
				"Action",
				"NodeId",
				"TestPackage");
		this.storageService.recordMeasurement(measurement);
	}
}
