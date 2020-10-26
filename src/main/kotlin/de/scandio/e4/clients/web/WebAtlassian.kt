package de.scandio.e4.clients.web

import de.scandio.e4.E4Env
import org.openqa.selenium.*
import java.lang.Exception
import java.net.URI

abstract class WebAtlassian(
        driver: WebDriver,
        base: URI,
        inputDir: String,
        outputDir: String,
        username: String,
        password: String
): AbstractWebClient(driver, base, inputDir, outputDir, username, password) {

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

    open fun awaitSuccessFlag() {
        dom.awaitElementPresent(".aui-flag[aria-hidden=\"false\"] .aui-message-success")
    }

    fun awaitErrorFlag(bodyContains: String) {
        dom.awaitElementPresent(".aui-flag[aria-hidden=\"false\"] .aui-message-error")
        val elem = dom.findElement(".aui-flag[aria-hidden=\"false\"] .aui-message-error")
        assert(elem.text.contains(bodyContains))
    }

    open fun activateAuiToggle(selector: String) {
        val uncheckedToggle = dom.findElements("$selector:not([checked])")
        if (uncheckedToggle.isNotEmpty()) {
            uncheckedToggle[0].click()
            dom.awaitMilliseconds(200)
        }
    }

    open fun deactivateAuiToggle(selector: String) {
        val checkedToggle = dom.findElements("$selector[checked]")
        if (checkedToggle.isNotEmpty()) {
            checkedToggle[0].click()
            dom.awaitMilliseconds(200)
        }
    }

}