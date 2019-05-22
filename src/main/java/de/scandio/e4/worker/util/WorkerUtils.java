package de.scandio.e4.worker.util;

import de.scandio.e4.confluence.web.WebConfluence;
import de.scandio.e4.worker.confluence.rest.RestConfluence;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.interfaces.WebClient;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.net.URI;

public class WorkerUtils {

	public static WebClient newChromeWebClient(String targetUrl, String screenshotDir, String username, String password) throws Exception {
		WebDriverManager.chromedriver().setup();
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		WebDriver driver = new ChromeDriver(chromeOptions);
		return new WebConfluence(driver, new URI(targetUrl), screenshotDir, username, password);
	}

	public static WebClient newPhantomJsWebClient(String targetUrl, String screenshotDir, String username, String password) throws Exception {
		WebDriverManager.phantomjs().setup();
		WebDriver driver = new PhantomJSDriver();
		return new WebConfluence(driver, new URI(targetUrl), screenshotDir, username, password);
	}

	public static RestClient newRestClient(String targetUrl, String username, String password) {
		RestClient restClient = new RestConfluence(targetUrl, username, password);
		return restClient;
	}

	public static String getRuntimeName() {
		Thread currentThread = Thread.currentThread();
		return currentThread.getName() + "-" + currentThread.getId();
	}
}
