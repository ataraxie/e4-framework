package de.scandio.e4.worker.services;

import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.worker.factories.ClientFactory;
import de.scandio.e4.worker.model.E4Error;
import de.scandio.e4.worker.model.E4Measurement;
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

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Service
public class TestRunnerService {

	private static final Logger log = LoggerFactory.getLogger(TestRunnerService.class);
	private final ApplicationStatusService applicationStatusService;
	private final StorageService storageService;
	private final UserCredentialsService userCredentialsService;

	// TODO: Make thread-safe (?)
	private static int numFinishedThreads = 0;
	private static int numFailedActions = 0;
	private static int numActionsExecuted = 0;

	private long mainThreadStartTime;

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

		log.info(">>> MAIN E4 THREAD: Running test package {{}} against URL {{}}", testPackageKey, targetUrl);

		final Class<TestPackage> testPackageClass = (Class<TestPackage>) Class.forName(testPackageKey);
		final TestPackage testPackage = testPackageClass.newInstance();
		final VirtualUserCollection virtualUserCollection = testPackage.getVirtualUsers();

		final List<VirtualUser> virtualUsers = new ArrayList<>();
		final int numConcurrentUsers = config.getNumConcurrentUsers();
		final int numWorkers = config.getNumWorkers();

		final List<Thread> virtualUserThreads = new ArrayList<>();
		final int numVirtualUsersThisWorker = numConcurrentUsers / numWorkers;

		final int workerIndex = storageService.getWorkerIndex();

		log.info(">>> MAIN E4 THREAD: This worker with index {{}} needs to start {{}} users.", workerIndex, numVirtualUsersThisWorker);

		for (Class<? extends VirtualUser> virtualUserClass : virtualUserCollection) {
			double weight = virtualUserCollection.getWeight(virtualUserClass);
			double numInstances = numConcurrentUsers * weight;
			for (int i = 0; i < numInstances; i++) {
				virtualUsers.add(createVirtualUser(virtualUserClass, config, testPackage));
			}
		}

		logVirtualUsers(virtualUsers);

		int vuserIndex = 0;
		for (VirtualUser vuser : virtualUsers) {
			if (vuserIndex % numWorkers == workerIndex) {
				final Thread virtualUserThread = createUserThread(testPackage, vuser, config);
				virtualUserThreads.add(virtualUserThread);
				log.info("Created user thread: {{}}", vuser.getClass().getSimpleName());
			}
			vuserIndex++;
		}

		this.mainThreadStartTime = new Date().getTime();
		final int numVirtualUsers = virtualUserThreads.size();
		final int randomStartDelayMaxMs = numVirtualUsers * 500; // e.g. 25 sec for 50 users, 125 sec for 250 users

		log.info(">>> MAIN E4 THREAD: random start delay max interval for all {{}} threads is {{}}sec", numVirtualUsers, randomStartDelayMaxMs / 1000);

		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(virtualUserThreads.size());
		for (Thread thread : virtualUserThreads) {
			executor.schedule(thread, new Random().nextInt(randomStartDelayMaxMs), TimeUnit.MILLISECONDS);
		}

		applicationStatusService.setTestsStatus(TestsStatus.RUNNING);


		while (numFinishedThreads < numVirtualUsers) {
			log.info(">>> MAIN E4 THREAD: Checking finished threads: {{}}", numFinishedThreads);
			Thread.sleep(3000);
		}

		log.info(">>> MAIN E4 THREAD: All {{}} threads are finished. {{}} actions were executed with {{}} errors. Your database is at {{}}", numVirtualUsers, numActionsExecuted, numFailedActions, storageService.getDatabaseFilePath());

		applicationStatusService.setTestsStatus(TestsStatus.FINISHED);
	}

	private VirtualUser createVirtualUser(
			Class<? extends VirtualUser> virtualUserClass,
			WorkerConfig config,
			TestPackage testPackage) throws Exception {

		VirtualUser virtualUser = virtualUserClass.newInstance();

		UserCredentials userCredentials;
		if (virtualUser.isAdminRequired()) {
			userCredentials = new UserCredentials(config.getUsername(), config.getPassword());
		} else {
			userCredentials = userCredentialsService.getRandomUser();
		}

		virtualUser.setImpersonatedUser(userCredentials);

		log.debug("Creating virtual user {{}} with actual user {{}}", virtualUserClass.getSimpleName(), userCredentials.getUsername());


		RestClient initialRestClient = ClientFactory.newRestClient(
				testPackage.getApplicationName(),
				storageService,
				config.getTarget(),
				userCredentials.getUsername(),
				userCredentials.getPassword());

		virtualUser.onInit(initialRestClient);
		return virtualUser;
	}

	private void logVirtualUsers(List<VirtualUser> virtualUsers) {
		log.info("Created the following {{}} virtual users for test package...", virtualUsers.size());
		int vuserIndex = 0;
		for (VirtualUser virtualUser : virtualUsers) {
			log.info("{}: {}", vuserIndex, virtualUser.getClass().getSimpleName());
			vuserIndex++;
		}
	}

	private Thread createUserThread(TestPackage testPackage, VirtualUser virtualUser, WorkerConfig config) {
		final String targetUrl = config.getTarget();
		final long durationInSeconds = config.getDurationInSeconds();

		log.info("Executing virtual user {{}} with actual user {{}}", virtualUser.getClass().getSimpleName(), virtualUser.getImpersonatedUser().getUsername());

		final Thread virtualUserThread = new Thread(() -> {
			try {
				final long delayInSeconds = (new Date().getTime() - this.mainThreadStartTime) / 1000;
				log.info("Thread for user {{}} started after {{}}sec", virtualUser.getImpersonatedUser().getUsername(), delayInSeconds);

				if (durationInSeconds > 0) {
					while (true) {
						long timePassedSinceStart = new Date().getTime() - this.mainThreadStartTime;
						if (timePassedSinceStart < durationInSeconds * 1000) {
							log.info("{{}}sec have passed since start which is below {{}}sec. Running again.", timePassedSinceStart / 1000, durationInSeconds);
							numActionsExecuted += 1;
							runActions(testPackage, virtualUser, durationInSeconds, targetUrl);
						} else {
							log.info("{{}}sec have passed since start which is above {{}}sec. Stopping.", timePassedSinceStart / 1000, durationInSeconds);
							numFinishedThreads += 1;
							break;
						}
					}
				} else {
					runActions(testPackage, virtualUser, durationInSeconds, targetUrl);
				}

			} catch (Exception e) {
				E4Error e4Error = new E4Error(
						"CREATE_CLIENTS",
						e.getClass().getSimpleName(),
						virtualUser.getClass().getSimpleName(),
						"");
				try {
					storageService.recordError(e4Error);
				} catch (Exception ex) {
					log.error("Could not record error in database");
				}
				log.error("Could not create WebClient and/or RestClient for VirtualUser thread", e);
			}

		});
		virtualUserThread.setDaemon(true);
		return virtualUserThread;
	}

	// TODO: as obvious, too many params
	private void runActions(TestPackage testPackage, VirtualUser virtualUser, long durationInSeconds, String targetUrl) throws Exception {
		ActionCollection actions = virtualUser.getActions();
		log.debug("Running {{}} actions for virtual user", actions.size());
		String username = virtualUser.getImpersonatedUser().getUsername();
		String password = virtualUser.getImpersonatedUser().getPassword();
		for (Action action : actions) {
			String runtime = WorkerUtils.getRuntimeName();
			String vuserClass = virtualUser.getClass().getSimpleName();
			String actionClass = action.getClass().getSimpleName();
			String testpackageClass = testPackage.getClass().getSimpleName();
			WebClient webClient = null;
			RestClient restClient;
			try {
				long workerTimeRunning = new Date().getTime() - this.mainThreadStartTime;
				if (workerTimeRunning > durationInSeconds * 1000) {
					log.info("Worker has been running longer than {{}}sec. Stopping.",  durationInSeconds);
					break;
				}
				log.debug("Executing action {{}}", action.getClass().getSimpleName());
				webClient = ClientFactory.newChromeWebClient(testPackage.getApplicationName(), targetUrl, applicationStatusService.getInputDir(),
						applicationStatusService.getOutputDir(), username, password);
				restClient = ClientFactory.newRestClient(testPackage.getApplicationName(), storageService, targetUrl, username, password);
				action.executeWithRandomDelay(webClient, restClient);
				if (log.isDebugEnabled() && new Date().getTime() % 10 == 0) {
					String screenshotPath = webClient.takeScreenshot("afteraction-" + action.getClass().getSimpleName());
					webClient.dumpHtml("afteraction-" + action.getClass().getSimpleName());
					log.info("Sample screenshot (and html with same path): {{}}", screenshotPath);
				}

				final long timeTaken = action.getTimeTaken();
				final String nodeId = action.getNodeId(webClient);
				E4Measurement measurement = new E4Measurement(
						timeTaken,
						runtime,
						vuserClass,
						actionClass,
						nodeId,
						testpackageClass);
				storageService.recordMeasurement(measurement);
			} catch (Exception e) {
				log.error("FAILED ACTION: {{}} with exception type {{}} and message {{}}",
						action.getClass().getSimpleName(), e.getClass().getSimpleName(), e.getMessage(), e);
				E4Error e4error = new E4Error("ACTION_FAILED",
						e.getClass().getName(),
						virtualUser.getClass().getSimpleName(),
						action.getClass().getSimpleName());
				storageService.recordError(e4error);
				numFailedActions += 1;
				if (webClient != null) {
					webClient.takeScreenshot("failed-action");
					webClient.dumpHtml("failed-action");
				}

			} finally {
				if (webClient != null) {
					webClient.quit();
				}
			}
		}
	}
}
