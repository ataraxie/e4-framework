package de.scandio.e4.worker.services;

import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
import de.scandio.e4.worker.collections.ActionCollection;
import de.scandio.e4.worker.confluence.rest.RestConfluence;
import de.scandio.e4.worker.interfaces.Action;
import de.scandio.e4.worker.interfaces.TestPackage;
import de.scandio.e4.worker.interfaces.WebClient;
import de.scandio.e4.worker.util.UserCredentials;
import de.scandio.e4.worker.util.WorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PreparationService {
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
        final WebClient webClient = WorkerUtils.newChromeWebClient(config.getTarget(), applicationStatusService.getScreenshotsDir(), config.getUsername(), config.getPassword());
        final RestConfluence restConfluence = (RestConfluence) WorkerUtils.newRestClient(config.getTarget(), config.getUsername(), config.getPassword());

        try {
            List<String> usernames = restConfluence.getConfluenceUsers();
            List<UserCredentials> userCredentials = new ArrayList<>();
            for (String username : usernames) {
                userCredentials.add(new UserCredentials(username, username)); // TODO: passwords!
            }
            userCredentialsService.storeUsers(userCredentials);

            for (Action action : setupScenarios) {
                action.execute(webClient, restConfluence);
                System.out.println("Finished prep action "+ action.getClass().getSimpleName());
            }

            System.out.println("[E4W] Preparations are finished!");
            applicationStatusService.setPreparationStatus(PreparationStatus.FINISHED);
        } catch (Exception ex) {
            log.error("Preparation Action failed.");
            ex.printStackTrace();
            applicationStatusService.setPreparationStatus(PreparationStatus.ERROR);
        }
    }

}
