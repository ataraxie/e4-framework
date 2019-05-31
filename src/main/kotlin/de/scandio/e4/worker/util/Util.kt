package de.scandio.e4.worker.util

import org.apache.commons.io.FileUtils
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import java.io.File

class Util {

    private val log = LoggerFactory.getLogger(javaClass)

    fun takeScreenshot(driver: WebDriver, screenshotPath: String): String {
        val ts = driver as TakesScreenshot
        val source: File = ts.getScreenshotAs(OutputType.FILE)
        log.info(screenshotPath)
        val destination = File(screenshotPath)
        FileUtils.copyFile(source, destination)
        return screenshotPath
    }

    fun dumpHtml(driver: WebDriver, dumpPath: String): String {
        FileUtils.writeStringToFile(File(dumpPath), driver.pageSource, "UTF-8", false);
        log.info(dumpPath)
        return dumpPath
    }
}