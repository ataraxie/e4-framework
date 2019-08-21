package de.scandio.e4.worker.interfaces;

import de.scandio.e4.helpers.DomHelper;
import org.openqa.selenium.WebDriver;

public interface WebClient {

	WebDriver getWebDriver();

	DomHelper getDomHelper();

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
