package de.scandio.e4.worker.services;

import de.scandio.e4.dto.PreparationStatus;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.interfaces.Scenario;
import de.scandio.e4.worker.interfaces.TestPackage;
import de.scandio.e4.worker.interfaces.WebClient;
import de.scandio.e4.worker.util.WorkerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PreparationService {
    private static final Logger log = LoggerFactory.getLogger(PreparationService.class);

    private final ApplicationStatusService applicationStatusService;

    public PreparationService(ApplicationStatusService applicationStatusService) {
        this.applicationStatusService = applicationStatusService;
    }

    public void prepare(Map<String, Object> parameters) throws Exception {
        System.out.println("[E4W] Starting to prepare...");
        applicationStatusService.setPreparationStatus(PreparationStatus.ONGOING);
        applicationStatusService.setConfig(parameters);




        // TODO: read from CommandLine / applicationProperties and not here
        final String screenshotDir = (String) parameters.get("screenshotDir");





        final Map<String, Object> config = applicationStatusService.getConfig();
        final String testPackageKey = (String) config.get("testPackage");
        final String targetUrl = (String) config.get("target");
        final String username = (String) config.get("username");
        final String password = (String) config.get("password");

        log.info("Running prepare scenarios of package {{}} against URL {{}}", testPackageKey, targetUrl);

        final Class<TestPackage> testPackage = (Class<TestPackage>) Class.forName(testPackageKey);
        final TestPackage testPackageInstance = testPackage.newInstance();
        final List<Scenario> setupScenarios = testPackageInstance.getSetupScenarios();
        final WebClient webClient = WorkerUtils.newWebClient(targetUrl, screenshotDir);
        final RestClient restClient = WorkerUtils.newRestClient(targetUrl, username, password);

        for (Scenario scenario : setupScenarios) {
            scenario.execute(webClient, restClient);
        }



        // TODO: create users
        // TODO: store the user credentials
        // TODO: log into every user once (to verify credentials) and click through the first time login intro
        // We need Selenium / REST calls for this
        // This should somehow be defined in a scenario as well

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }



        System.out.println("[E4W] Preparations are finished...");
        applicationStatusService.setPreparationStatus(PreparationStatus.FINISHED);
    }

}
