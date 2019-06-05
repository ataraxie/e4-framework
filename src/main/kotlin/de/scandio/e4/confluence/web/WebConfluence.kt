package de.scandio.e4.confluence.web

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.worker.interfaces.WebClient
import org.apache.commons.io.FileUtils
import org.openqa.selenium.*
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Exception
import java.net.URI
import java.net.URLEncoder
import java.util.*

class WebConfluence(
        val driver: WebDriver,
        val base: URI,
        val outputDir: String,
        val username: String,
        val password: String
): WebClient {


    override fun getUser(): String {
        return this.username
    }

    private val log = LoggerFactory.getLogger(javaClass)
    var dom: DomHelper = DomHelper(driver)

    override fun getWebDriver(): WebDriver {
        return this.driver
    }

    override fun quit() {
        this.driver.quit()
    }

    override fun getNodeId(): String {
        var nodeId = ""
        try {
            nodeId = dom.findElement("#footer-cluster-node").text
            nodeId = nodeId.replace("(", "").replace(")", "").replace(" ", "")
        } catch (e: Exception) {
            log.warn("Could not obtain node ID from footer. Leaving blank.")
        }
        return nodeId
    }

    fun getDomHelper(): DomHelper {
        return this.dom
    }

    override fun login() {
        // Do the following if you want to do it only initially when the browser is opened
        // if (driver.currentUrl.equals("about:blank") || driver.currentUrl.equals("data:,")) { // only login once!
        navigateTo("login.action")
        dom.awaitElementPresent("form[name='loginform'], .login-section p.last, #main-content", 10)
        if (dom.isElementPresent("form[name='loginform']")) {
            dom.insertText("#os_username", this.username)
            dom.insertText("#os_password", this.password)
            dom.click("#loginButton")
            try {
                dom.awaitElementPresent(".pagebody", 20)
                if (dom.isElementPresent("#dashboard-onboarding-dialog")) {
                    dom.click("#dashboard-onboarding-dialog .aui-button-primary")
                    dom.awaitMilliseconds(50)
                }
            } catch (e: TimeoutException) {
                dom.click("#grow-intro-video-skip-button", 5)
                dom.click("#grow-ic-content button[data-action='skip']")
                dom.click(".intro-find-spaces-relevant-spaces label:first-child .intro-find-spaces-space")
                dom.awaitMilliseconds(1000)
                dom.click(".intro-find-spaces-button-continue")
                dom.awaitElementPresent(".pagebody", 20)
            }
        } else {
            log.debug("Went to login screen but was already logged in")
        }
    }

    fun authenticateAdmin() {
        navigateTo("authenticate.action?destination=/admin/viewgeneralconfig.action")
        dom.insertText("#password", password)
        dom.click("#authenticateButton")
        dom.awaitElementPresent("#admin-navigation")
    }

    fun navigateTo(path: String) {
        log.info("[SELENIUM] Navigating to {{}} with current URL {{}}", path, driver.currentUrl)
        if (!driver.currentUrl.endsWith(path)) {
            driver.navigate().to(base.resolve(path).toURL())
        } else {
            log.info("[SELENIUM] Already on page")
        }

    }

    override fun takeScreenshot(screenshotName: String): String {
        var dest = ""
        try {
            val ts = driver as TakesScreenshot
            val source: File = ts.getScreenshotAs(OutputType.FILE)
            dest = "$outputDir/$screenshotName-${Date().time}.png"
            log.info("[SCREENSHOT] {{}}", dest)
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
        } catch (e: Exception) {
            log.warn("FAILED TO CREATE SCREENSHOT WITH EXCEPTION: " + e.javaClass.simpleName + " " + e.message)
        }

        return dest
    }

    fun goToSpaceHomepage(spaceKey: String) {
        navigateTo("display/$spaceKey")
        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]")
    }

    fun goToPage(pageId: Long) {
        navigateTo("pages/viewpage.action?pageId=$pageId")
        dom.awaitElementPresent("#main-content")
    }


    fun goToPage(spaceKey: String, pageTitle: String) {
        val encodedPageTitle = URLEncoder.encode(pageTitle, "UTF-8")
        navigateTo("display/$spaceKey/$encodedPageTitle")
        dom.awaitElementPresent("#main-content")
    }

    fun goToEditPage() {
        dom.awaitElementClickable("#editPageLink")
        dom.click("#editPageLink")
        dom.awaitElementPresent("#inviteToEditLink", 30)
    }

    fun goToBlogpost(spaceKey: String, blogpostTitle: String, blogpostCreationDate: String) {
        val encodedTitle = URLEncoder.encode(blogpostTitle, "UTF-8")
        navigateTo("display/$spaceKey/$blogpostCreationDate/$encodedTitle")
    }

    fun insertMacro(macroId: String, macroSearchTerm: String) {
        dom.click("#rte-button-insert")
        dom.click("#rte-insert-macro")
        dom.insertText("#macro-browser-search", macroSearchTerm)
        dom.click("#macro-$macroId")
        dom.click("#macro-details-page button.ok", 5)
        dom.awaitElementClickable("#rte-button-publish")
    }

    fun savePage() {
        dom.click("#rte-button-publish")
        dom.awaitElementPresent("#main-content")
    }

    fun createDefaultPage(spaceKey: String, pageTitle: String) {
        val loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Integer eget aliquet nibh praesent. Platea dictumst quisque sagittis purus sit amet volutpat consequat mauris. Montes nascetur ridiculus mus mauris vitae ultricies leo integer. In fermentum posuere urna nec. Viverra vitae congue eu consequat ac felis. Sed egestas egestas fringilla phasellus faucibus scelerisque eleifend donec pretium. Non diam phasellus vestibulum lorem sed risus ultricies. Amet tellus cras adipiscing enim eu turpis egestas pretium. A pellentesque sit amet porttitor eget dolor morbi. Integer quis auctor elit sed vulputate mi sit amet. Leo in vitae turpis massa sed elementum tempus egestas. Non odio euismod lacinia at quis risus sed vulputate odio. Nunc scelerisque viverra mauris in. Tortor at risus viverra adipiscing at. Bibendum at varius vel pharetra vel turpis."
        navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#content-title-div")
        dom.insertText("#content-title", pageTitle)
        dom.click("#wysiwygTextarea_ifr")
        dom.insertTextTinyMce("<h1>Lorem Ipsum</h1><p>$loremIpsum</p>")
        dom.click("#rte-button-publish")
        dom.awaitElementPresent("#main-content")
    }

    fun createEmptySpace(spaceKey: String, spaceName: String) {
        navigateTo("spaces/createspace-start.action")
        dom.awaitElementPresent("#create-space-form")
        dom.insertText("#create-space-form input[name='key']", spaceKey)
        dom.insertText("#create-space-form input[name='name']", spaceName)
        dom.awaitAttributeNotPresent("#create-space-form .aui-button[name='create']", "disabled")
        dom.awaitMilliseconds(1000) // TODO: Not sure why this anymore
        dom.click("#create-space-form .aui-button[name='create']")
        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]")
    }

    fun disablePlugin(pluginKey: String) {
        login()
        authenticateAdmin()
        val upmRowSelector = ".upm-plugin[data-key='$pluginKey']"
        log.info("Disabling plugin: $pluginKey")
        navigateTo("plugins/servlet/upm/manage/all")
        dom.awaitElementPresent(".upm-plugin-list-container", 40)
        dom.click(upmRowSelector, 40)
        dom.click("$upmRowSelector .aui-button[data-action='DISABLE']", 40)
        dom.awaitElementPresent("$upmRowSelector .aui-button[data-action='ENABLE']")
        log.info("--> SUCCESS")
    }

    fun installPlugin(absoluteFilePath: String) {
        login()
        authenticateAdmin()
        log.info("Installing ${absoluteFilePath.split('/').last()}")
        navigateTo("plugins/servlet/upm")
        dom.awaitElementPresent(".upm-plugin-list-container", 40)
        dom.click("#upm-upload", 40)
        log.info("-> Waiting for upload dialog...")
        dom.awaitElementPresent("#upm-upload-file", 40)
        dom.findElement("#upm-upload-file").sendKeys(absoluteFilePath)
        dom.click("#upm-upload-dialog button.confirm", 40)
        log.info("-> Waiting till upload is fully done...")
        dom.awaitClass("#upm-manage-container", "loading", 40)
        dom.awaitNoClass("#upm-manage-container", "loading", 40)
        log.info("--> SUCCESS")
    }



    fun setLogLevel(packagePath: String, logLevel: String) {
        login()
        authenticateAdmin()
        navigateTo("admin/viewlog4j.action")
        dom.insertText("[name='extraClassName']", packagePath)
        dom.click("[name='extraLevelName'] option[value='$logLevel']")
        dom.click("#addEntryButton")
        dom.awaitElementPresent("[id='$packagePath']")
    }

    fun disableSecurityCheckbox(checkboxSelector: String) {
        login()
        authenticateAdmin()
        log.info("Disabling security checkbox $checkboxSelector")
        navigateTo("admin/editsecurityconfig.action")
        dom.click(checkboxSelector)
        dom.click("#confirm")
        dom.awaitElementPresent("form[action='editsecurityconfig.action']")
    }

    fun disableSecureAdminSessions() {
        disableSecurityCheckbox("#webSudoEnabled")
    }

    fun disableCaptchas() {
        disableSecurityCheckbox("#enableElevatedSecurityCheck")
    }

}