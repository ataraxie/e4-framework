package de.scandio.e4.util

import de.scandio.e4.worker.util.WorkerUtils
import org.apache.commons.io.FileUtils
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import java.io.File

class Util {

    fun takeScreenshot(driver: WebDriver, screenshotPath: String): String {
        val ts = driver as TakesScreenshot
        val source: File = ts.getScreenshotAs(OutputType.FILE)
        System.out.println(screenshotPath)
        val destination = File(screenshotPath)
        FileUtils.copyFile(source, destination)
        return screenshotPath
    }

    fun dumpHtml(driver: WebDriver, dumpPath: String): String {
        FileUtils.writeStringToFile(File(dumpPath), driver.pageSource, "UTF-8", false);
        System.out.println(dumpPath)
        return dumpPath
    }
}