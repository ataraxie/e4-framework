package de.scandio.e4.worker.util;

import de.scandio.e4.confluence.web.WebConfluence;
import de.scandio.e4.worker.confluence.rest.RestConfluence;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.interfaces.WebClient;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorkerUtils {

	public static WebClient newChromeWebClient(String targetUrl, String outputDir, String username, String password) throws Exception {
		WebDriverManager.chromedriver().setup();
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("--disable-impl-side-painting");
		WebDriver driver = new ChromeDriver(chromeOptions);
		return new WebConfluence(driver, new URI(targetUrl), outputDir, username, password);
	}

//	public static WebClient newPhantomJsWebClient(String targetUrl, String outputDir, String username, String password) throws Exception {
//		WebDriverManager.phantomjs().setup();
//		WebDriver driver = new PhantomJSDriver();
//		driver.manage().window().setSize(new Dimension(1400, 1000));
//		return new WebConfluence(driver, new URI(targetUrl), outputDir, username, password);
//	}

	public static RestClient newRestClient(String targetUrl, String username, String password) {
		RestClient restClient = new RestConfluence(targetUrl, username, password);
		return restClient;
	}

	public static String getRuntimeName() {
		Thread currentThread = Thread.currentThread();
		return currentThread.getName() + "-" + currentThread.getId();
	}

	public static <T> List<T> getRandomItems(List<T> items, int howMany) {
		List<T> ret = new ArrayList<>();
		int size = items.size();
		for (int i = 0; i < howMany; i++) {
			int rnd = new Random().nextInt(size);
			ret.add(items.get(rnd));
		}
		return ret;
	}

	public static <T> T getRandomItem(List<T> items) {
		return getRandomItems(items, 1).get(0);
	}
}
