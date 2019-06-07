package de.scandio.e4.confluence.web

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.RandomData
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
        dom.awaitMilliseconds(500) // safety
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

    fun navigateToLoginIfNecessary(path: String) {
        navigateTo(path)
        takeScreenshot("navigateToLoginIfNecessary-1")
        dom.awaitElementClickable("#full-height-container")
        takeScreenshot("navigateToLoginIfNecessary-2")
        if (dom.isElementPresent("form[name='loginform']")) {
            dom.insertText("#os_username", this.username)
            dom.insertText("#os_password", this.password)
            dom.click("#loginButton")
        }
        dom.awaitSeconds(3)
        takeScreenshot("navigateToLoginIfNecessary-3")
        if (dom.isElementPresent("#authenticateButton")) {
            dom.insertText("#password", password)
            dom.click("#authenticateButton")
            dom.awaitElementPresent("#admin-navigation")
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

    fun search(searchString: String) {
        navigateTo("search/searchv3.action")
        dom.awaitElementPresent("#query-string")
        dom.insertText("#query-string", searchString)
        log.debug("Searching for string $searchString")
        dom.click("#search-query-submit-button")
        dom.awaitElementPresent(".search-results")
        dom.awaitElementInsivible(".search-blanket")
    }

    fun savePage() {
        dom.click("#rte-button-publish")
        dom.awaitElementPresent("#main-content")
    }

    fun createDefaultPage(pageTitleBeginning: String) {
        dom.click("#quick-create-page-button")
        dom.awaitElementPresent("#wysiwyg")
        val pageTitle = "$pageTitleBeginning $username (${Date().time})"
        log.debug("Creating page with title $pageTitle")
        publishDefaultPage(pageTitle)
    }

    fun createDefaultPage(spaceKey: String, pageTitle: String) {
        navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        publishDefaultPage(pageTitle)
    }

    private fun setPageTitleInEditor(pageTitle: String) {
        dom.click("#content-title-div")
        dom.insertText("#content-title", pageTitle)
    }

    private fun publishDefaultPage(pageTitle: String) {
        if (dom.isElementPresent("#closeDisDialog")) {
            dom.click("#closeDisDialog")
            dom.awaitMilliseconds(100)
        }
        setPageTitleInEditor(pageTitle)
        dom.addTextTinyMce("<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>")
        savePage()
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
        val upmRowSelector = ".upm-plugin[data-key='$pluginKey']"
        log.info("Disabling plugin: $pluginKey")
        navigateToLoginIfNecessary("plugins/servlet/upm/manage/all")
        takeScreenshot("disable-plugin-1")
        dom.awaitElementPresent(".upm-plugin-list-container", 40)
        takeScreenshot("disable-plugin-2")
        dom.click(upmRowSelector, 40)
        takeScreenshot("disable-plugin-3")
        dom.click("$upmRowSelector .aui-button[data-action='DISABLE']", 40)
        takeScreenshot("disable-plugin-4")
        dom.awaitElementPresent("$upmRowSelector .aui-button[data-action='ENABLE']")
        log.info("--> SUCCESS")
    }

    fun installPlugin(absoluteFilePath: String, pluginKey: String) {
        log.info("Installing ${absoluteFilePath.split('/').last()}")
        navigateToLoginIfNecessary("plugins/servlet/upm")
        takeScreenshot("install-plugin-1")
        dom.awaitElementClickable(".upm-plugin-list-container", 40)
        takeScreenshot("install-plugin-2")
        dom.click("#upm-upload", 40)
        takeScreenshot("install-plugin-3")
        log.info("-> Waiting for upload dialog...")
        dom.awaitElementClickable("#upm-upload-file", 40)
        takeScreenshot("install-plugin-4")
        dom.findElement("#upm-upload-file").sendKeys(absoluteFilePath)
        dom.click("#upm-upload-dialog button.confirm", 40)
        takeScreenshot("install-plugin-5")
        log.info("-> Waiting till upload is fully done...")
        dom.awaitClass("#upm-manage-container", "loading", 40)
        dom.awaitNoClass("#upm-manage-container", "loading", 40)
        takeScreenshot("install-plugin-6")
        log.info("--> SUCCESS (we think, but please check!)")
    }

    fun disableMarketplaceConnectivity() {
        navigateToLoginIfNecessary("plugins/servlet/upm")
        dom.click("#link-bar-settings a", 30)
        dom.click("#upm-checkbox-pacDisabled", 30)
        dom.click("#upm-settings-dialog .aui-button.confirm")
    }

    fun setLogLevel(packagePath: String, logLevel: String) {
        navigateToLoginIfNecessary("admin/viewlog4j.action")
        dom.insertText("[name='extraClassName']", packagePath)
        dom.click("[name='extraLevelName'] option[value='$logLevel']")
        dom.click("#addEntryButton")
        dom.awaitElementPresent("[id='$packagePath']")
    }

    fun disableSecurityCheckbox(checkboxSelector: String) {
        log.info("Disabling security checkbox $checkboxSelector")
        navigateToLoginIfNecessary("admin/editsecurityconfig.action")
        dom.click(checkboxSelector)
        dom.click("#confirm")
        dom.awaitSeconds(10) // TODO: below doesn't work
//        dom.awaitElementPresent("form[action='editsecurityconfig.action']", 50)
    }

    fun disableSecureAdminSessions() {
        disableSecurityCheckbox("#webSudoEnabled")
    }

    fun disableCaptchas() {
        disableSecurityCheckbox("#enableElevatedSecurityCheck")
    }

    fun editPageAddContent(contentId: Long, htmlContentToAdd: String) {
        log.debug("Adding content {{}} to content {{}}", htmlContentToAdd, contentId)
        navigateTo("pages/editpage.action?pageId=$contentId")
        dom.awaitElementPresent("#wysiwyg")
        dom.addTextTinyMce(htmlContentToAdd)
        dom.awaitMilliseconds(50)
        savePage()
    }

    fun addRandomComment(htmlComment: String) {
        log.debug("Adding comment {{}} to content {{}}", htmlComment)
        dom.click(".quick-comment-prompt")
        dom.awaitElementPresent("#wysiwyg")
        dom.insertTextTinyMce(htmlComment)
        saveComment()
    }

    private fun saveComment() {
        dom.click("#rte-button-publish")
        dom.awaitElementPresent(".comment.focused")
    }

}