package de.scandio.e4.worker.services;

import de.scandio.e4.client.config.WorkerConfig;
import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.dto.TestsStatus;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.interfaces.Scenario;
import de.scandio.e4.worker.interfaces.TestPackage;
import de.scandio.e4.worker.interfaces.WebClient;
import de.scandio.e4.worker.util.WorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PreparationService {
    private static final Logger log = LoggerFactory.getLogger(PreparationService.class);

    private final ApplicationStatusService applicationStatusService;

    public PreparationService(ApplicationStatusService applicationStatusService) {
        this.applicationStatusService = applicationStatusService;
    }

    public void prepare(WorkerConfig config) throws Exception {
        if (applicationStatusService.getTestsStatus().equals(TestsStatus.RUNNING)) {
            throw new Exception("Can not prepare while tests are running!");
        }

        System.out.println("[E4W] Preparing...");
        applicationStatusService.setPreparationStatus(PreparationStatus.ONGOING);
        applicationStatusService.setConfig(config);

        log.info("Running prepare scenarios of package {{}} against URL {{}}", config.getTestPackage(), config.getTarget());

        final Class<TestPackage> testPackage = (Class<TestPackage>) Class.forName(config.getTestPackage());
        final TestPackage testPackageInstance = testPackage.newInstance();
        final List<Scenario> setupScenarios = testPackageInstance.getSetupScenarios();
        final WebClient webClient = WorkerUtils.newWebClient(config.getTarget(), applicationStatusService.getScreenshotsDir());
        final RestClient restClient = WorkerUtils.newRestClient(config.getTarget(), config.getUsername(), config.getPassword());

        try {

            // TODO: create users here and give them to the credentials service
            // TODO: or just accept user credentials in worker config ??? every virtual user needs a user
            // TODO: log into every user once (to verify credentials) and click through the first time login intro

            for (Scenario scenario : setupScenarios) {
                scenario.execute(webClient, restClient);
                System.out.println("Finished prep scenario "+scenario.getClass().getSimpleName());
            }

            System.out.println("[E4W] Preparations are finished!");
            applicationStatusService.setPreparationStatus(PreparationStatus.FINISHED);
        } catch (Exception ex) {
            log.error("Preparation Scenario failed.");
            ex.printStackTrace();
            applicationStatusService.setPreparationStatus(PreparationStatus.ERROR);
        }
    }

}
