package de.scandio.e4.clients.web

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.worker.factories.ClientFactory
import de.scandio.e4.worker.interfaces.WebClient
import org.apache.commons.io.FileUtils
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Exception
import java.net.URI
import java.util.*
import org.apache.commons.io.filefilter.TrueFileFilter
import java.util.regex.Pattern
import kotlin.collections.ArrayList


abstract class AbstractWebClient(
        var driver: WebDriver,
        val base: URI,
        val inputDir: String,
        val outputDir: String,
        val username: String,
        val password: String
): WebClient {

    val log = LoggerFactory.getLogger(javaClass)

    var dom: DomHelper = DomHelper(driver)

    // TODO: this is a bit weird because the original driver came from outside in the constructor..
    override fun refreshDriver() {
        this.driver = ClientFactory.newChromeDriver()
    }

    override fun getUser(): String {
        return this.username
    }

    override fun getWebDriver(): WebDriver {
        return this.driver
    }

    override fun quit() {
        this.driver.quit()
    }


    override fun takeScreenshot(screenshotName: String): String {
        var dest = ""
        try {
            val ts = driver as TakesScreenshot
            val source: File = ts.getScreenshotAs(OutputType.FILE)
            dest = "$outputDir/$screenshotName-${Date().time}.png"
            log.info("[SCREENSHOT] {{}}", dest)
            println(dest)
            val destination = File(dest)
            FileUtils.copyFile(source, destination)
        } catch (e: Exception) {
            log.warn("FAILED TO CREATE SCREENSHOT WITH EXCEPTION: " + e.javaClass.simpleName)
        }

        return dest
    }

    override fun dumpHtml(dumpName: String): String {
        var dest = ""
        try {
            dest = "$outputDir/$dumpName-${Date().time}.html"
            FileUtils.writeStringToFile(File(dest), driver.pageSource, "UTF-8", false);
            log.info("[DUMP] {{}}", dest)
            println(dest)
        } catch (e: Exception) {
            log.warn("FAILED TO CREATE SCREENSHOT WITH EXCEPTION: " + e.javaClass.simpleName + " " + e.message)
        }

        return dest
    }

    override fun navigateTo(path: String) {
        log.info("[SELENIUM] Navigating to {{}} with current URL {{}}", path, driver.currentUrl)
        if (!driver.currentUrl.endsWith(path)) {
            driver.navigate().to(base.resolve(path).toURL())
        } else {
            log.info("[SELENIUM] Already on page")
        }
    }

    override fun navigateToBaseUrl() {
        driver.navigate().to(base.toString())
    }

    fun getFilesFromInputDir(filenameRegex: String): List<File> {
        val regex = Pattern.compile(filenameRegex)
        val allFiles = FileUtils.listFiles(File(inputDir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE) as List<File>
        val targetFiles = ArrayList<File>()
        for (file in allFiles) {
            if (regex.matcher(file.name).matches()) {
                targetFiles.add(file)
            }
        }
        return targetFiles
    }

    fun getDomHelper(): DomHelper {
        return this.dom
    }

}