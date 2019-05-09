package de.scandio.e4;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;

public class MainClass {

	public static void main(String[] args) throws Exception {
		WebDriverManager.chromedriver().setup();
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		WebDriver driver = new ChromeDriver(chromeOptions);
		driver.navigate().to("http://localhost:8090/");
		takeScreenshot(driver, "test1");
	}

	public static String takeScreenshot (WebDriver driver, String screenshotName) {

		try {
			TakesScreenshot ts = (TakesScreenshot)driver;
			File source = ts.getScreenshotAs(OutputType.FILE);
			String dest = "/Users/fgrund/tmp/selenium/" + screenshotName + ".png";
			System.out.println(dest);
			File destination = new File(dest);
			FileUtils.copyFile(source, destination);
			return dest;
		}

		catch (IOException e) {
			return e.getMessage();
		}
	}


}
