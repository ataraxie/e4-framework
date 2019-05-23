package de.scandio.e4.worker.services;

import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
import de.scandio.e4.worker.collections.VirtualUserCollection;
import de.scandio.e4.worker.collections.VirtualUserWithWeight;
import de.scandio.e4.worker.interfaces.*;
import de.scandio.e4.worker.util.WorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class TestRunnerService {
	private static final Logger log = LoggerFactory.getLogger(TestRunnerService.class);
	private final ApplicationStatusService applicationStatusService;
	private final StorageService storageService;

	private final String USERNAME = "admin"; // TODO!!
	private final String PASSWORD = "admin"; // TODO!!

	public TestRunnerService(ApplicationStatusService applicationStatusService, StorageService storageService) {
		this.applicationStatusService = applicationStatusService;
		this.storageService = storageService;
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
		final VirtualUserCollection virtualUsers = testPackageInstance.getVirtualUsers();

		log.debug("Found {{}} virtual users for test package", virtualUsers.size());


		final List<Thread> virtualUserThreads = new ArrayList<>();

		log.info("This worker needs to start " + config.getVirtualUsers() + " users.");

		for (int i = 0; i < config.getVirtualUsers(); i++) {
			final VirtualUserWithWeight virtualUserWithWeight = virtualUsers.get(i % virtualUsers.size());
			final VirtualUser virtualUser = virtualUserWithWeight.getVirtualUser();
			final Thread virtualUserThread = createUserThread(virtualUser, config);
			virtualUserThreads.add(virtualUserThread);
			log.info("Created user thread: "+virtualUser.getClass().getSimpleName());
		}

		virtualUserThreads.forEach(thread -> {
			try {
				Thread.sleep(new Random().nextInt(50));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			thread.start();
		});
		applicationStatusService.setTestsStatus(TestsStatus.RUNNING);

		// TODO: check whether tests need to be repeated instead of just blindly waiting for the threads
		System.out.println("Waiting for tests to finish...");
		for (Thread virtualUserThread : virtualUserThreads) {
			virtualUserThread.join();
		}
		System.out.println("All tests are finished!");

		applicationStatusService.setTestsStatus(TestsStatus.FINISHED);
	}

	private Thread createUserThread(VirtualUser virtualUser, WorkerConfig config) {
		final String targetUrl = config.getTarget();

		final Thread virtualUserThread = new Thread(() -> {
			log.debug("Executing virtual user {{}}", virtualUser.getClass().getSimpleName());

			// TODO: this might need to be an infinite loop later if repeatTests == true
			for (Action action : virtualUser.getActions()) {
				WebClient webClient = null;
				try {
					// TODO: right now only using hardcoded admin - later use UserCredentialsService
					webClient = WorkerUtils.newPhantomJsWebClient(targetUrl, applicationStatusService.getScreenshotsDir(), USERNAME, PASSWORD);
					final RestClient restClient = WorkerUtils.newRestClient(targetUrl, USERNAME, PASSWORD);

					log.debug("Executing action {{}}", action.getClass().getSimpleName());

					action.execute(webClient, restClient);
					final long timeTaken = action.getTimeTaken();
					storageService.recordMeasurement(virtualUser, action, Thread.currentThread(), timeTaken);
				} catch (Exception e) {
					log.error("FAILED SCENARIO: "+action.getClass().getSimpleName());
					// TODO: recordMeasurement action as failed somewhere
					e.printStackTrace();
				} finally {
					if (webClient != null) {
						webClient.quit();
					}
				}
			}
		});
		virtualUserThread.setDaemon(true);
		return virtualUserThread;
	}

}
