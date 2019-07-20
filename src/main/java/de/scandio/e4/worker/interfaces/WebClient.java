package de.scandio.e4.worker.interfaces;

import org.openqa.selenium.WebDriver;

public interface WebClient {

	WebDriver getWebDriver();

	String takeScreenshot(String screenshotName);

	String dumpHtml(String dumpName);

	void quit();

	void login();

	void authenticateAdmin();

	String getNodeId();

	String getUser();

	void refreshDriver();

	void navigateTo(String path);
	void navigateToBaseUrl();

}
