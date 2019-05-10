package de.scandio.e4.worker.services;

import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
import de.scandio.e4.worker.interfaces.*;
import de.scandio.e4.worker.util.WorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class TestRunnerService {
	private static final Logger log = LoggerFactory.getLogger(TestRunnerService.class);
	private final ApplicationStatusService applicationStatusService;

	public TestRunnerService(ApplicationStatusService applicationStatusService) {
		this.applicationStatusService = applicationStatusService;
	}

	public void stopTests() throws Exception {
		// TODO stop all currently running runnables.
		throw new Exception("stopping tests not yet implemented");
	}

	public synchronized void runTestPackage() throws Exception {
		if (!applicationStatusService.getPreparationStatus().equals(PreparationStatus.FINISHED)) {
			throw new Exception("Can't run test package when preparations are not finished!");
		}

		if (applicationStatusService.getTestsStatus().equals(TestsStatus.RUNNING)) {
			throw new Exception("There is already a TestPackage running!");
		}

		final WorkerConfig config = applicationStatusService.getConfig();
		final String testPackageKey = config.getTestPackage();
		final String targetUrl = config.getTarget();

		log.info("Running test package {{}} against URL {{}}", testPackageKey, targetUrl);

		final Class<TestPackage> testPackage = (Class<TestPackage>) Class.forName(testPackageKey);
		final TestPackage testPackageInstance = testPackage.newInstance();
		final List<? extends VirtualUser> virtualUsers = testPackageInstance.getVirtualUsers();

		log.debug("Found {{}} virtual users for test package", virtualUsers.size());

		applicationStatusService.setTestsStatus(TestsStatus.RUNNING);

		final List<Thread> virtualUserThreads = new ArrayList<>();

		for (VirtualUser virtualUser : virtualUsers) {

			final Thread virtualUserThread = new Thread(() -> {
				log.debug("Executing virtual user {{}}", virtualUser.getClass().getSimpleName());

				// 1 virtual == 1 thread TODO: make a new thread for this user
				// The threads need to be saved somewhere so we can stop them again
				// Whether there are saved threads can also be used to determine if there is a test running


				// TODO: this might need to be an infinite loop later if repeatTests == true
				for (Scenario scenario : virtualUser.getScenarios()) {
					try {
						// TODO: right now only using hardcoded admin - later use UserCredentialsService
						final WebClient webClient = WorkerUtils.newWebClient(targetUrl, applicationStatusService.getScreenshotsDir());
						final RestClient restClient = WorkerUtils.newRestClient(targetUrl, "admin", "admin");

						log.debug("Executing scenario {{}}", scenario.getClass().getSimpleName());

						scenario.execute(webClient, restClient);
						scenario.getTimeTaken();
					} catch (Exception e) {
						log.error("FAILED SCENARIO: "+scenario.getClass().getSimpleName());
						// TODO: record scenario as failed somewhere
						e.printStackTrace();
					}
				}
			});
			virtualUserThread.start();
			virtualUserThreads.add(virtualUserThread);
		}


		// TODO: check whether tests need to be repeated instead of just blindly waiting for the threads
		System.out.println("Waiting for tests to finish...");
		for (Thread virtualUserThread : virtualUserThreads) {
			virtualUserThread.join();
		}
		System.out.println("All tests are finished!");

		applicationStatusService.setTestsStatus(TestsStatus.FINISHED);
	}

}
