package de.scandio.e4;

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper;
import de.scandio.e4.confluence.web.WebConfluence;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class MainClass {

	private static final String URL = "http://localhost:8090/";
	private static final String SCREENSHOT_DIR = "/tmp/selenium";

	public static void main(String[] args) throws Exception {
		WebDriverManager.chromedriver().setup();
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		WebDriver driver = new ChromeDriver(chromeOptions);
		WebConfluence webConfluence = new WebConfluence(driver, new URI(URL), SCREENSHOT_DIR);
		MainKotlin mainKotlin = new MainKotlin(driver, webConfluence, new DomHelper(webConfluence));
		mainKotlin.execute();
	}

}
