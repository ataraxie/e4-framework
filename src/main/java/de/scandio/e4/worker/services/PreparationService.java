package de.scandio.e4.worker.services;

import de.scandio.e4.worker.abstracts.RestConfluence;
import de.scandio.e4.worker.abstracts.WebConfluence;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.interfaces.Scenario;
import de.scandio.e4.worker.interfaces.TestPackage;
import de.scandio.e4.worker.interfaces.WebClient;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreparationService {
    private static final Logger log = LoggerFactory.getLogger(PreparationService.class);

    private boolean preparationsAreFinished = false;

    public void reset() {
        preparationsAreFinished = false;
    }

    public boolean arePreparationsFinished() {
        return preparationsAreFinished;
    }

    public void prepare(String targetUrl, String testPackageKey, String username, String password) throws Exception {
        System.out.println("[E4W] Starting to prepare...");

        log.info("Running prepare for package {{}} against URL {{}}", testPackageKey, targetUrl);

        final Class<TestPackage> testPackage = (Class<TestPackage>) Class.forName(testPackageKey);
        final TestPackage testPackageInstance = testPackage.newInstance();
        final List<Scenario> setupScenarios = testPackageInstance.getSetupScenarios();

        for (Scenario scenario : setupScenarios) {
            WebClient webClient = createWebClient(targetUrl);
            RestClient restClient = createRestClient(targetUrl, username, password);
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
        preparationsAreFinished = true;
    }

    private WebClient createWebClient(String targetUrl) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        WebDriver driver = new ChromeDriver(chromeOptions);
        return new WebConfluence(driver);
    }

    private RestClient createRestClient(String targetUrl, String username, String password) {
        RestClient restClient = new RestConfluence(targetUrl, username, password);
        return restClient;
    }
}
