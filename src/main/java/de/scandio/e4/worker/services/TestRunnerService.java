package de.scandio.e4.worker.services;

import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
import de.scandio.e4.worker.collections.ActionCollection;
import de.scandio.e4.worker.collections.VirtualUserCollection;
import de.scandio.e4.worker.interfaces.*;
import de.scandio.e4.worker.util.UserCredentials;
import de.scandio.e4.worker.util.WorkerUtils;
import de.scandio.e4.worker.util.WorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


@Service
public class TestRunnerService {
	private static final Logger log = LoggerFactory.getLogger(TestRunnerService.class);
	private final ApplicationStatusService applicationStatusService;
	private final StorageService storageService;
	private final UserCredentialsService userCredentialsService;

	public TestRunnerService(ApplicationStatusService applicationStatusService, StorageService storageService, UserCredentialsService userCredentialsService) {
		this.applicationStatusService = applicationStatusService;
		this.storageService = storageService;
		this.userCredentialsService = userCredentialsService;
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
		final VirtualUserCollection virtualUserCollection = testPackageInstance.getVirtualUsers();

		final List<VirtualUser> virtualUsers = new ArrayList<>();
		final int numConcurrentUsers = config.getNumConcurrentUsers();
		final int numWorkers = config.getNumWorkers();
		for (Class<? extends VirtualUser> virtualUserClass : virtualUserCollection) {
			double weight = virtualUserCollection.getWeight(virtualUserClass);
			double numInstances = numConcurrentUsers * weight;
			for (int i = 0; i < numInstances; i++) {
				virtualUsers.add(virtualUserClass.newInstance());
			}
		}

		log.debug("Created {{}} virtual users for test package", virtualUsers.size());

		final List<Thread> virtualUserThreads = new ArrayList<>();
		final int numVirtualUsersThisWorker = numConcurrentUsers / numWorkers;

		final List<UserCredentials> allUserCredentials = userCredentialsService.getAllUsers();
		final UserCredentials userCredentials = WorkerUtils.getRandomItem(allUserCredentials);
		final int workerIndex = storageService.getWorkerIndex();

		log.info("This worker with index {{}} needs to start {{}} users.", workerIndex, numVirtualUsersThisWorker);

		for (int i = 0; i < numVirtualUsersThisWorker; i++) {
			if (workerIndex + i > virtualUsers.size()) {
				log.info("No more virtual users to run for worker with index {{}}", workerIndex);
				break;
			}
			final VirtualUser virtualUser = virtualUsers.get(workerIndex + i);
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

	private Thread createUserThread(VirtualUser virtualUser, WorkerConfig config) throws Exception {
		final String targetUrl = config.getTarget();
		final long threadStartTime = new Date().getTime();
		final long durationInSeconds = config.getDurationInSeconds();

		final Thread virtualUserThread = new Thread(() -> {
			try {
				final UserCredentials randomUser = userCredentialsService.getRandomUser();
				final String username = randomUser.getUsername();
				final String password = randomUser.getPassword();
				final WebClient webClient = WorkerUtils.newChromeWebClient(targetUrl, applicationStatusService.getScreenshotsDir(), username, password);
				final RestClient restClient = WorkerUtils.newRestClient(targetUrl, username, password);

				log.info("Executing virtual user {{}} with actual user {{}}", virtualUser.getClass().getSimpleName(), username);

				if (durationInSeconds > 0) {
					while (true) {
						long timePassedSinceStart = new Date().getTime() - threadStartTime;
						if (timePassedSinceStart < durationInSeconds * 1000) {
							log.info("{{}}ms have passed since start which is belog {{}}sec. Running again.", timePassedSinceStart, durationInSeconds);
							runActions(virtualUser, webClient, restClient, threadStartTime, durationInSeconds);
						} else {
							log.info("{{}}ms have passed since start which is above {{}}sec. Stopping.", timePassedSinceStart, durationInSeconds);
							webClient.quit();
							break;
						}
					}
				} else {
					runActions(virtualUser, webClient, restClient, threadStartTime, durationInSeconds);
				}
			} catch (Exception e) {
				log.error("Could not create WebClient and/or RestClient for VirtualUser thread", e);
			}

		});
		virtualUserThread.setDaemon(true);
		return virtualUserThread;
	}

	private void runActions(VirtualUser virtualUser, WebClient webClient, RestClient restClient, long threadStartTime, long durationInSeconds) {
		ActionCollection actions = virtualUser.getActions(webClient, restClient);
		log.info("Running {{}} actions for virtual user", actions.size());
		for (Action action : actions) {
			try {
				long workerTimeRunning = new Date().getTime() - threadStartTime;
				if (workerTimeRunning > durationInSeconds * 1000) {
					log.info("Worker has been running longer than {{}} seconds. Stopping.",  durationInSeconds);
					break;
				}
				log.debug("Executing action {{}}", action.getClass().getSimpleName());

				action.execute(webClient, restClient);
				final long timeTaken = action.getTimeTaken();
				storageService.recordMeasurement(virtualUser, action, Thread.currentThread(), timeTaken);
			} catch (Exception e) {
				log.error("FAILED SCENARIO: "+action.getClass().getSimpleName());
				System.out.println(webClient.takeScreenshot("failed-scenario"));
				// TODO: recordMeasurement action as failed somewhere
				e.printStackTrace();
			}
		}
	}

}
