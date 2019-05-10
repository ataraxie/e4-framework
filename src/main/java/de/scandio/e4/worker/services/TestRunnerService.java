package de.scandio.e4.worker.services;

import de.scandio.e4.worker.interfaces.Scenario;
import de.scandio.e4.worker.interfaces.TestPackage;
import de.scandio.e4.worker.interfaces.VirtualUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static de.scandio.e4.E4Application.*;

import java.util.List;


@Service
public class TestRunnerService {

	private static final Logger log = LoggerFactory.getLogger(TestRunnerService.class);

	private final UserCredentialsService userCredentialsService;

	private String currentlyRunningTestPackage;

	public TestRunnerService(UserCredentialsService userCredentialsService) {
		this.userCredentialsService = userCredentialsService;
	}

	public boolean areTestsRunning() {
		return currentlyRunningTestPackage != null;
	}

	public void stopTests() throws Exception {
		// TODO stop all currently running runnables.
		throw new Exception("stopping tests not yet implemented");
	}

	public synchronized void runTestPackage(String targetUrl, String testPackageKey) throws Exception {
		// TODO: Test if no other package is running first
		if (areTestsRunning()) {
			throw new IllegalStateException("Can't start a new TestPackage when another TestPackage is already running.");
		}

		log.info("Running test package {{}} against URL {{}}", testPackageKey, targetUrl);

		currentlyRunningTestPackage = testPackageKey;

		final Class<TestPackage> testPackage = (Class<TestPackage>) Class.forName(testPackageKey);
		final TestPackage testPackageInstance = testPackage.newInstance();
		final List<? extends VirtualUser> virtualUsers = testPackageInstance.getVirtualUsers();

		log.debug("Found {{}} virtual users for test package", virtualUsers.size());

		for (VirtualUser virtualUser : virtualUsers) {

			// 1 virtual == 1 thread TODO: make a new thread for this user
			// The threads need to be saved somewhere so we can stop them again
			// Whether there are saved threads can also be used to determine if there is a test running

			// TODO: assign a username to a virtualUser (so we can have a logged in user for the scenarios)

			final List<Scenario> scenarios = virtualUser.getScenarios();

			for (Scenario scenario : scenarios) {
				try {
					//scenario.execute();
					scenario.getTimeTaken();
				} catch (Exception e) {
					// record scenario as failed
					e.printStackTrace();
				}
			}
		}
	}

}
