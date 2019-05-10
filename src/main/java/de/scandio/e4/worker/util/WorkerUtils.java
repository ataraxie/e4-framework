package de.scandio.e4.worker.util;

import de.scandio.e4.confluence.web.WebConfluence;
import de.scandio.e4.worker.confluence.rest.RestConfluence;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.interfaces.Scenario;
import de.scandio.e4.worker.interfaces.WebClient;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URI;
import java.util.Date;

public class WorkerUtils {

	public static WebClient newWebClient(String targetUrl, String screenshotDir) throws Exception {
		WebDriverManager.chromedriver().setup();
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		WebDriver driver = new ChromeDriver(chromeOptions);
		return new WebConfluence(driver, new URI(targetUrl), screenshotDir);
	}

	public static RestClient newRestClient(String targetUrl, String username, String password) {
		RestClient restClient = new RestConfluence(targetUrl, username, password);
		return restClient;
	}

	public static long runAndMeasure(WebClient webClient, RestClient restClient, Scenario scenario) throws Exception {
		long start = new Date().getTime();
		scenario.execute(webClient, restClient);
		return new Date().getTime() - start;

	}
}
