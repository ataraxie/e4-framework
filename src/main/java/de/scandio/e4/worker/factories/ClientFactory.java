package de.scandio.e4.worker.factories;

import de.scandio.e4.clients.WebConfluence;
import de.scandio.e4.clients.WebJira;
import de.scandio.e4.worker.client.ApplicationName;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.interfaces.WebClient;
import de.scandio.e4.worker.rest.RestConfluence;
import de.scandio.e4.worker.rest.RestJira;
import de.scandio.e4.worker.services.StorageService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URI;

public class ClientFactory {


	public static WebDriver newChromeDriver() {
		WebDriverManager.chromedriver().setup();
		ChromeOptions chromeOptions = new ChromeOptions();
//		chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE); // https://www.skptricks.com/2018/08/timed-out-receiving-message-from-renderer-selenium.html
		chromeOptions.addArguments("start-maximized");
		chromeOptions.addArguments("disable-extensions");
		chromeOptions.addArguments("enable-automation");
		chromeOptions.addArguments("--start-maximized");
		chromeOptions.addArguments("--disable-extensions");
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("--disable-impl-side-painting");
		chromeOptions.addArguments("--no-sandbox");
		chromeOptions.addArguments("--disable-infobars"); //https://stackoverflow.com/a/43840128/1689770
		chromeOptions.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/50725918/1689770
		chromeOptions.addArguments("--disable-browser-side-navigation"); //https://stackoverflow.com/a/49123152/1689770
		chromeOptions.addArguments("--disable-gpu"); //https://stackoverflow.com/questions/51959986/how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc
		return new ChromeDriver(chromeOptions);
	}

	public static WebClient newChromeWebClient(ApplicationName appName, String baseUrl,
					String inputDir, String outputDir, String username, String password) throws Exception {
		WebDriver driver = newChromeDriver();
		driver.manage().window().setSize(new Dimension(2000, 1500));
		URI uri = new URI(baseUrl);
		WebClient webClient;
		if (appName == ApplicationName.confluence) {
			webClient = new WebConfluence(driver, uri, inputDir, outputDir, username, password);
		} else {
			webClient = new WebJira(driver, uri, inputDir, outputDir, username, password);
		}
		return webClient;
	}

//	public static WebClient newPhantomJsWebClient(String targetUrl, String outputDir, String username, String password) throws Exception {
//		WebDriverManager.phantomjs().setup();
//		WebDriver driver = new PhantomJSDriver();
//		driver.manage().window().setSize(new Dimension(1400, 1000));
//		return new WebConfluence(driver, new URI(targetUrl), outputDir, username, password);
//	}

	public static RestClient newRestClient(ApplicationName applicationName, StorageService storageService, String baseUrl, String username, String password) {
		if (applicationName == ApplicationName.confluence) {
			return new RestConfluence(storageService, baseUrl, username, password);
		} else {
			return new RestJira(storageService, baseUrl, username, password);
		}
	}

}
