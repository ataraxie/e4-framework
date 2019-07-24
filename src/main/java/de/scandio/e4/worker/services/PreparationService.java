package de.scandio.e4.worker.services;

import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
import de.scandio.e4.worker.client.NoopWebClient;
import de.scandio.e4.worker.collections.ActionCollection;
import de.scandio.e4.worker.factories.ClientFactory;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.interfaces.Action;
import de.scandio.e4.worker.interfaces.TestPackage;
import de.scandio.e4.worker.interfaces.WebClient;
import de.scandio.e4.worker.util.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PreparationService {

    private static final String DEFAULT_USER_PASSWORD = "password"; // TODO: extract to client config!

    private static final Logger log = LoggerFactory.getLogger(PreparationService.class);

    private final ApplicationStatusService applicationStatusService;
    private final UserCredentialsService userCredentialsService;
    private final StorageService storageService;

    public PreparationService(ApplicationStatusService applicationStatusService,
                              UserCredentialsService userCredentialsService,
                              StorageService storageService) {
        this.applicationStatusService = applicationStatusService;
        this.userCredentialsService = userCredentialsService;
        this.storageService = storageService;
    }

    public void prepare(int workerIndex, WorkerConfig config) throws Exception {
        if (applicationStatusService.getTestsStatus().equals(TestsStatus.RUNNING)) {
            throw new Exception("Can not prepare while tests are running!");
        }

        log.info("[E4W] Preparing worker with index {{}} ...", workerIndex);
        applicationStatusService.setPreparationStatus(PreparationStatus.ONGOING);
        applicationStatusService.setConfig(config);

        storageService.setWorkerIndex(workerIndex);

        log.info("Running prepare actions of package {{}} against URL {{}}", config.getTestPackage(), config.getTarget());

        final Class<TestPackage> testPackage = (Class<TestPackage>) Class.forName(config.getTestPackage());
        final TestPackage testPackageInstance = testPackage.newInstance();

        final ActionCollection setupScenarios = testPackageInstance.getSetupActions();
        final RestClient restClient = ClientFactory.newRestClient(testPackageInstance.getApplicationName(), storageService,
				config.getTarget(), config.getUsername(), config.getPassword());

        try {
            List<String> usernames = restClient.getUsernames();
			List<UserCredentials> userCredentials = new ArrayList<>();
			for (String username : usernames) {
				if (!username.equals(config.getUsername())) {
					userCredentials.add(new UserCredentials(username, DEFAULT_USER_PASSWORD));
				}
			}
			userCredentialsService.storeUsers(userCredentials);
            if (!setupScenarios.isEmpty()) {
				WebClient webClient;
            	if (setupScenarios.allRestOnly()) {
					webClient = new NoopWebClient();
				} else {
					webClient = ClientFactory.newChromeWebClient(testPackageInstance.getApplicationName(),
							config.getTarget(), applicationStatusService.getInputDir(),
							applicationStatusService.getOutputDir(), config.getUsername(), config.getPassword());
				}
				try {
					for (Action action : setupScenarios) {
						try {
							log.info("Executing action {{}}", action.getClass().getSimpleName());
							action.execute(webClient, restClient);
						} catch (Exception e) {
							log.error("Failed executing action {{}}", action.getClass().getSimpleName());
							if (!action.isRestOnly()) {
								webClient.takeScreenshot("failed-action");
								webClient.dumpHtml("failed-action");
							}
							throw e;
						}

						log.info("Finished prep action "+ action.getClass().getSimpleName());
					}
				} finally {
					webClient.quit();
				}
			} else {
            	log.info("No setup scenarios given.");
			}

            log.info("[E4W] Preparations are finished!");
            applicationStatusService.setPreparationStatus(PreparationStatus.FINISHED);
        } catch (Exception ex) {
            log.error("Preparation Action failed.");
            ex.printStackTrace();
            applicationStatusService.setPreparationStatus(PreparationStatus.ERROR);
        }
    }

}
