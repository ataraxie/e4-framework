package de.scandio.e4.clients

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.worker.factories.ClientFactory
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.RandomData
import de.scandio.e4.worker.util.WorkerUtils
import org.apache.commons.io.FileUtils
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Exception
import java.net.URI
import java.net.URLEncoder
import java.util.*

abstract class WebAtlassian(
        var driver: WebDriver,
        val base: URI,
        val inputDir: String,
        val outputDir: String,
        val username: String,
        val password: String
): WebClient {

    val log = LoggerFactory.getLogger(javaClass)
    var dom: DomHelper = DomHelper(driver)

    constructor(oldWebAtlassian: WebAtlassian) : this(
            oldWebAtlassian.driver, oldWebAtlassian.base, oldWebAtlassian.inputDir,
            oldWebAtlassian.outputDir, oldWebAtlassian.username, oldWebAtlassian.password
    )

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

    override fun getNodeId(): String {
        var nodeId = ""
        var nodeKey = ""
        var nodeName = ""
        try {
            nodeKey = dom.findElement("meta[name='confluence-cluster-node-id']").getAttribute("value")
            nodeName = dom.findElement("meta[name='confluence-cluster-node-name']").getAttribute("value")
            nodeId = "$nodeName:$nodeKey"
        } catch (e: Exception) {
            try {
                log.warn("Could not determine node ID from meta tags. Trying with footer.", e)
                nodeId = dom.findElement("#footer-cluster-node").text
                nodeId = nodeId.replace("(", "").replace(")", "").replace(" ", "")
            } catch (e: Exception) {
                takeScreenshot("missing-nodeid")
                dumpHtml("missing-nodeid")
                log.warn("Could not obtain node ID neither from meta tags nor footer. Leaving blank.", e)
            }
        }
        return nodeId
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

    fun installPlugin(pluginName: String, pluginVersion: String, pluginLicense: String = "", pluginKey: String = "") {
        val absoluteFilePath = "$inputDir/$pluginName-$pluginVersion.jar"
        log.info("Installing ${absoluteFilePath.split('/').last()}")
        navigateTo("plugins/servlet/upm")
        debugScreen("install-plugin-1")
        dom.awaitElementClickable(".upm-plugin-list-container", 40)
        debugScreen("install-plugin-2")
        dom.click("#upm-upload", 40)
        debugScreen("install-plugin-3")
        log.info("-> Waiting for upload dialog...")
        dom.awaitElementClickable("#upm-upload-file", 40)
        debugScreen("install-plugin-4")
        dom.findElement("#upm-upload-file").sendKeys(absoluteFilePath)
        dom.click("#upm-upload-dialog button.confirm", 40)
        debugScreen("install-plugin-5")
        log.info("-> Waiting till upload is fully done...")
        dom.awaitClass("#upm-manage-container", "loading", 40)
        dom.awaitNoClass("#upm-manage-container", "loading", 40)
        debugScreen("install-plugin-6")
        log.info("--> SUCCESS (we think, but please check!)")
        if (!pluginLicense.isEmpty() && !pluginKey.isEmpty()) {
            val rowSelector = ".upm-plugin[data-key='$pluginKey']"
            val licenseSelector = "$rowSelector textarea.edit-license-key"
            dom.awaitElementClickable(licenseSelector)
            dom.click("#upm-plugin-status-dialog .cancel")
            dom.insertText(licenseSelector, pluginLicense)
            dom.awaitSeconds(5) // TODO
            dom.click("$rowSelector .submit-license")
            dom.awaitSeconds(5) // TODO
        }
    }

    fun debugScreen(snapshotName: String) {
        if (log.isDebugEnabled) {
            takeScreenshot(snapshotName)
            dumpHtml(snapshotName)
        }
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

    fun getDomHelper(): DomHelper {
        return this.dom
    }

}