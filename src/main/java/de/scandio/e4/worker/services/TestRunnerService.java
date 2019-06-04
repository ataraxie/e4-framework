package de.scandio.e4.worker.services;

import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.Measurement;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
import de.scandio.e4.worker.collections.ActionCollection;
import de.scandio.e4.worker.collections.VirtualUserCollection;
import de.scandio.e4.worker.interfaces.*;
import de.scandio.e4.worker.util.UserCredentials;
import de.scandio.e4.worker.util.WorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

		logVirtualUsers(virtualUsers);

		final List<Thread> virtualUserThreads = new ArrayList<>();
		final int numVirtualUsersThisWorker = numConcurrentUsers / numWorkers;

		final List<UserCredentials> allUserCredentials = userCredentialsService.getAllUsers();
		final UserCredentials userCredentials = WorkerUtils.getRandomItem(allUserCredentials);
		final int workerIndex = storageService.getWorkerIndex();

		log.info("This worker with index {{}} needs to start {{}} users.", workerIndex, numVirtualUsersThisWorker);

		int vuserIndex = 0;
		for (VirtualUser vuser : virtualUsers) {
			if (vuserIndex % numWorkers == workerIndex) {
				final Thread virtualUserThread = createUserThread(testPackageInstance, vuser, config);
				virtualUserThreads.add(virtualUserThread);
				log.info("Created user thread: {{}}", vuser.getClass().getSimpleName());
			}
			vuserIndex++;
		}

		virtualUserThreads.forEach(thread -> {
			try {
				Thread.sleep(5000);
				thread.start();
			} catch (InterruptedException e) {
				log.error("Could not start user thread", e);
			}

		});
		applicationStatusService.setTestsStatus(TestsStatus.RUNNING);

		log.info("Waiting for tests to finish...");
		for (Thread virtualUserThread : virtualUserThreads) {
			virtualUserThread.join();
		}
		log.info("All tests are finished!");

		applicationStatusService.setTestsStatus(TestsStatus.FINISHED);
	}

	private void logVirtualUsers(List<VirtualUser> virtualUsers) {
		log.info("Created the following {{}} virtual users for test package...", virtualUsers.size());
		int vuserIndex = 0;
		for (VirtualUser virtualUser : virtualUsers) {
			log.info("{}: {}", vuserIndex, virtualUser.getClass().getSimpleName());
			vuserIndex++;
		}
	}

	private Thread createUserThread(TestPackage testPackage, VirtualUser virtualUser, WorkerConfig config) throws Exception {
		final String targetUrl = config.getTarget();
		final long durationInSeconds = config.getDurationInSeconds();

		final Thread virtualUserThread = new Thread(() -> {
			try {
				final UserCredentials randomUser = userCredentialsService.getRandomUser();
				final String username = randomUser.getUsername();
				final String password = randomUser.getPassword();

				log.info("Executing virtual user {{}} with actual user {{}}", virtualUser.getClass().getSimpleName(), username);

				final long threadStartTime = new Date().getTime();

				if (durationInSeconds > 0) {
					while (true) {
						long timePassedSinceStart = new Date().getTime() - threadStartTime;
						if (timePassedSinceStart < durationInSeconds * 1000) {
							log.info("{{}}ms have passed since start which is below {{}}sec. Running again.", timePassedSinceStart, durationInSeconds);
							runActions(testPackage, virtualUser, threadStartTime, durationInSeconds, targetUrl, username, password);
						} else {
							log.info("{{}}ms have passed since start which is above {{}}sec. Stopping.", timePassedSinceStart, durationInSeconds);
							//webClient.quit();
							break;
						}
					}
				} else {
					runActions(testPackage, virtualUser, threadStartTime, durationInSeconds, targetUrl, username, password);
				}

			} catch (Exception e) {
				log.error("Could not create WebClient and/or RestClient for VirtualUser thread", e);
			}

		});
		virtualUserThread.setDaemon(true);
		return virtualUserThread;
	}

	// TODO: as obvious, too many params
	private void runActions(TestPackage testPackage, VirtualUser virtualUser, long threadStartTime, long durationInSeconds, String targetUrl, String username, String password) throws Exception {
		final WebClient webClient = WorkerUtils.newChromeWebClient(targetUrl, applicationStatusService.getOutputDir(), username, password);
		final RestClient restClient = WorkerUtils.newRestClient(targetUrl, username, password);

		try {
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
					action.executeWithRandomDelay(webClient, restClient);
					//webClient.takeScreenshot("afteraction-" + action.getClass().getSimpleName());
					//webClient.dumpHtml("afteraction-" + action.getClass().getSimpleName());
					final long timeTaken = action.getTimeTaken();
					final String nodeId = action.getNodeId(webClient);
					Measurement measurement = new Measurement(
							timeTaken,
							WorkerUtils.getRuntimeName(),
							virtualUser.getClass().getSimpleName(),
							action.getClass().getSimpleName(),
							nodeId, testPackage.getClass().getSimpleName());
					storageService.recordMeasurement(measurement);
				} catch (Exception e) {
					log.error("FAILED ACTION: "+action.getClass().getSimpleName());
					webClient.takeScreenshot("failed-scenario");
					// TODO: recordMeasurement action as failed somewhere
					e.printStackTrace();
				}
			}
		} finally {
			webClient.quit();
		}
	}

}
