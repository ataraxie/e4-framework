package de.scandio.e4.worker.services;

import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.worker.interfaces.*;
import de.scandio.e4.worker.util.WorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class TestRunnerService {
	private static final Logger log = LoggerFactory.getLogger(TestRunnerService.class);
	private final UserCredentialsService userCredentialsService;
	private final ApplicationStatusService applicationStatusService;

	public TestRunnerService(UserCredentialsService userCredentialsService,
							 ApplicationStatusService applicationStatusService) {
		this.userCredentialsService = userCredentialsService;
		this.applicationStatusService = applicationStatusService;
	}

	public void stopTests() throws Exception {
		// TODO stop all currently running runnables.
		throw new Exception("stopping tests not yet implemented");
	}

	public synchronized void runTestPackage() throws Exception {
		// TODO: Also test if no other package is running
		if (!applicationStatusService.getPreparationStatus().equals(PreparationStatus.FINISHED)) {
			throw new Exception("Cant run test package when preparations are not finished!");
		}

		final Map<String, Object> config = applicationStatusService.getConfig();
		final String testPackageKey = (String) config.get("testPackage");
		final String targetUrl = (String) config.get("target");






		// TODO: read from CommandLine / applicationProperties and not here
		final String screenshotDir = (String) config.get("screenshotDir");






		log.info("Running test package {{}} against URL {{}}", testPackageKey, targetUrl);

		final Class<TestPackage> testPackage = (Class<TestPackage>) Class.forName(testPackageKey);
		final TestPackage testPackageInstance = testPackage.newInstance();
		final List<? extends VirtualUser> virtualUsers = testPackageInstance.getVirtualUsers();

		log.debug("Found {{}} virtual users for test package", virtualUsers.size());

		for (VirtualUser virtualUser : virtualUsers) {

			log.debug("Executing virtual user {{}}", virtualUser.getClass().getSimpleName());

			// 1 virtual == 1 thread TODO: make a new thread for this user
			// The threads need to be saved somewhere so we can stop them again
			// Whether there are saved threads can also be used to determine if there is a test running

			// TODO: assign a username to a virtualUser (so we can have a logged in user for the scenarios)
			// TODO: right now only using hardcoded admin - later use UserCredentialsService
			final String username = "admin";
			final String password = "admin";

			final List<Scenario> scenarios = virtualUser.getScenarios();



			final WebClient webClient = WorkerUtils.newWebClient(targetUrl, screenshotDir);
			// TODO: credentials!
			final RestClient restClient = WorkerUtils.newRestClient(targetUrl, "admin", "admin");

			for (Scenario scenario : scenarios) {
				log.debug("Executing scenario {{}}", scenario.getClass().getSimpleName());
				try {
					scenario.execute(webClient, restClient);
					scenario.getTimeTaken();
				} catch (Exception e) {
					// record scenario as failed
					e.printStackTrace();
				}
			}
		}
	}

}
