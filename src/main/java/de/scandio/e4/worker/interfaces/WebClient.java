package de.scandio.e4.worker.interfaces;

import org.openqa.selenium.WebDriver;

public interface WebClient {

	WebDriver getWebDriver();

	String takeScreenshot(String screenshotName);

	String dumpHtml(String dumpName);

	void quit();

	void login();

	String getNodeId();

}
