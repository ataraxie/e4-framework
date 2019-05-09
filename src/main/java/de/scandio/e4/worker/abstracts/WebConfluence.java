package de.scandio.e4.worker.abstracts;

import de.scandio.e4.worker.interfaces.WebClient;
import org.openqa.selenium.WebDriver;

public class WebConfluence implements WebClient {

	private WebDriver driver;

	public WebConfluence(WebDriver driver) {
		this.driver = driver;
	}

	@Override
	public WebDriver getDriver() {
		return driver;
	}

}
