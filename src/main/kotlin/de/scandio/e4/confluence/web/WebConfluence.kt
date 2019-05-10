package de.scandio.e4.confluence.web

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.enjoy.LoginPage
import de.scandio.e4.worker.interfaces.WebClient
import org.apache.commons.io.FileUtils
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URI
import java.net.URLEncoder

class WebConfluence(
        val driver: WebDriver,
        val base: URI,
        val screenshotDir: String
): WebClient {

    private val log = LoggerFactory.getLogger(javaClass)
    private val dom: DomHelper = DomHelper(this)

    override fun getWebDriver(): WebDriver {
        return this.driver
    }

    fun navigateTo(path: String) {
        log.info("[SELENIUM] Navigating to {{}}", path)
        driver.navigate().to(base.resolve(path).toURL())
    }

    fun navigateToPage(spaceKey: String, pageTitle: String) {
        val encodedPageTitle = URLEncoder.encode(pageTitle, "UTF-8")
        navigateTo("display/$spaceKey/$encodedPageTitle")
    }

    fun takeScreenshot(screenshotName: String): String {
        val ts = driver as TakesScreenshot
        val source: File = ts.getScreenshotAs(OutputType.FILE)
        val dest = "$screenshotDir/$screenshotName.png"
        log.info("[SCREENSHOT] {{}}", dest)
        System.out.println(dest)
        val destination: File = File(dest)
        FileUtils.copyFile(source, destination)
        return dest
    }

}