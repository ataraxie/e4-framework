package de.scandio.e4.clients

import de.scandio.e4.helpers.DomHelper
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
import kotlin.collections.HashMap

class WebConfluence(
        driver: WebDriver,
        base: URI,
        inputDir: String,
        outputDir: String,
        username: String,
        password: String
): WebAtlassian(driver, base, inputDir, outputDir, username, password) {

    override fun login() {
        // Do the following if you want to do it only initially when the browser is opened
        // if (driver.currentUrl.equals("about:blank") || driver.currentUrl.equals("data:,")) { // only login once!
        navigateTo("login.action")
        dom.awaitElementPresent("form[name='loginform'], .login-section p.last, #main-content", 40)
        if (dom.isElementPresent("form[name='loginform']")) {
            dom.insertText("#os_username", this.username)
            dom.insertText("#os_password", this.password)
            dom.click("#loginButton")
            try {
                dom.awaitElementPresent(".pagebody", 40)
                if (dom.isElementPresent("#dashboard-onboarding-dialog")) {
                    dom.click("#dashboard-onboarding-dialog .aui-button-primary")
                    dom.awaitMilliseconds(50)
                }
            } catch (e: TimeoutException) {
                dom.click("#grow-intro-video-skip-button", 40)
                dom.click("#grow-ic-content button[data-action='skip']")
                dom.click(".intro-find-spaces-relevant-spaces text:first-child .intro-find-spaces-space")
                dom.awaitMilliseconds(1000)
                dom.click(".intro-find-spaces-button-continue")
                dom.awaitElementPresent(".pagebody", 40)
            }
        } else {
            log.debug("Went to login screen but was already logged in")
        }
        dom.awaitMilliseconds(500) // safety
    }

    override fun authenticateAdmin() {
        navigateTo("authenticate.action?destination=/admin/viewgeneralconfig.action")
        dom.insertText("#password", password)
        dom.click("#authenticateButton")
        dom.awaitElementPresent("#admin-navigation")
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

    fun goToSpaceHomePage(spaceKey: String) {
        navigateTo("display/$spaceKey")
        dom.awaitElementPresent("#main-content")
    }

    fun goToEditPage() {
        dom.awaitElementClickable("#editPageLink")
        dom.click("#editPageLink")
        awaitEditPageLoaded()
    }

    fun awaitEditPageLoaded() {
        dom.awaitElementClickable("#content-title-div", 40)
    }

    fun goToBlogpost(spaceKey: String, blogpostTitle: String, blogpostCreationDate: String) {
        val encodedTitle = URLEncoder.encode(blogpostTitle, "UTF-8")
        navigateTo("display/$spaceKey/$blogpostCreationDate/$encodedTitle")
    }

    fun openMacroBrowser(macroId: String, macroSearchTerm: String) {
        log.debug("Trying to insert macro {{}}", macroId)
        driver.switchTo().frame("wysiwygTextarea_ifr")
        debugScreen("openMacroBrowser-0")
        dom.click("#tinymce")
        driver.switchTo().parentFrame()
        dom.click("#rte-button-insert")
        debugScreen("openMacroBrowser-1")
        dom.click("#rte-insert-macro")
        debugScreen("openMacroBrowser-2")
        dom.insertText("#macro-browser-search", macroSearchTerm)
        debugScreen("openMacroBrowser-3")
        dom.click("#macro-$macroId")
        debugScreen("openMacroBrowser-4")
    }

    fun saveMacroBrowser() {
        dom.click("#macro-details-page button.ok", 5)
        debugScreen("saveMacroBrowser-1")
        dom.awaitElementClickable("#rte-button-publish")
        dom.awaitMilliseconds(50)
    }

    fun insertMacro(macroId: String, macroSearchTerm: String, macroParameters: Map<String, String> = emptyMap()) {
        openMacroBrowser(macroId, macroSearchTerm)
        debugScreen("after-openMacroBrowser")
        if (!macroParameters.isEmpty()) {
            for ((paramKey, paramValue) in macroParameters) {
                val selector = "#macro-browser-dialog #macro-param-$paramKey"
                val elem = dom.findElement(selector)
                if ("select".equals(elem.tagName)) {
                    dom.setSelectedOption(selector, paramValue)
                } else {
                    dom.insertText(selector, paramValue)
                }
            }
        }
        debugScreen("after-setParams")
        saveMacroBrowser()
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
        dom.removeElementWithJQuery(".aui-blanket")
        dom.click("#rte-button-publish")
        dom.awaitElementPresent("#main-content")
    }

    private fun getPageId(): Number {
        return dom.executeScript("AJS.Meta.get(\"page-id\")").toString().toInt()
    }

    fun createDefaultPage(pageTitleBeginning: String) {
        dom.click("#quick-create-page-button")
        dom.awaitElementPresent("#wysiwyg")
        val pageTitle = "$pageTitleBeginning $username (${Date().time})"
        log.debug("Creating page with title $pageTitle")
        publishPage(pageTitle)
    }

    fun createDefaultPage(spaceKey: String, pageTitle: String) {
        navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        publishPage(pageTitle)
    }

    fun createCustomPage(spaceKey: String, pageTitle: String, pageContentHtml: String) {
        navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        publishPage(pageTitle, pageContentHtml)
    }

    fun setPageTitleInEditor(pageTitle: String) {
        dom.click("#content-title-div")
        dom.insertText("#content-title", pageTitle)
    }

    private fun publishPage(pageTitle: String, pageContentHtml: String = "") {
        var html = pageContentHtml
        if (html.isEmpty()) {
            html = "<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
        }
        if (dom.isElementPresent("#closeDisDialog")) {
            dom.click("#closeDisDialog")
            dom.awaitMilliseconds(100)
        }
        setPageTitleInEditor(pageTitle)
        dom.addTextTinyMce(html)
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
        navigateTo("plugins/servlet/upm/manage/all")
        debugScreen("disable-plugin-1")
        dom.awaitElementPresent(".upm-plugin-list-container", 40)
        debugScreen("disable-plugin-2")
        dom.click(upmRowSelector, 40)
        debugScreen("disable-plugin-3")
        dom.click("$upmRowSelector .aui-button[data-action='DISABLE']", 40)
        debugScreen("disable-plugin-4")
        dom.awaitElementPresent("$upmRowSelector .aui-button[data-action='ENABLE']")
        log.info("--> SUCCESS")
    }

    fun disableMarketplaceConnectivity() {
        navigateTo("plugins/servlet/upm")
        dom.click("#link-bar-settings a", 30)
        dom.click("#upm-checkbox-pacDisabled", 30)
        dom.click("#upm-settings-dialog .aui-button.confirm")
    }

    fun setLogLevel(packagePath: String, logLevel: String) {
        navigateTo("admin/viewlog4j.action")
        dom.insertText("[name='extraClassName']", packagePath)
        dom.click("[name='extraLevelName'] option[value='$logLevel']")
        dom.click("#addEntryButton")
        dom.awaitElementPresent("[id='$packagePath']")
    }

    fun disableSecurityCheckbox(checkboxSelector: String) {
        log.info("Disabling security checkbox $checkboxSelector")
        navigateTo("admin/editsecurityconfig.action")
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

    fun addSpaceGroupPermission(spaceKey: String, groupName: String, permissionKey: String, permitted: Boolean) {
        navigateTo("spaces/spacepermissions.action?key=$spaceKey")
        val selector = ".permissionCell[data-permission='$permissionKey'][data-permission-group='$groupName'][data-permission-set='${!permitted}']"
        dom.click("form[name='editspacepermissions'] #edit")
        dom.insertText("#groups-to-add-autocomplete", groupName)
        dom.click("input[name='groupsToAddButton']")
        dom.awaitSeconds(3) //TODO!!

        if (dom.isElementPresent(selector)) {
            dom.click(selector)
        }
        dom.click(".primary-button-container input[type='submit']")
    }

    fun insertMacroBody(macroId: String, htmlBody: String) {
        dom.insertHtmlInEditor(".wysiwyg-macro[data-macro-name='$macroId']", htmlBody)
    }

    fun actionBuilder(): Actions {
        return Actions(driver)
    }

    fun simulateBulletList(bulletPoints: Array<String>) {
        val actions = Actions(driver)
        bulletPoints.forEach {
            actions.sendKeys(it).sendKeys(Keys.RETURN)
        }
        actions.perform()
    }

    fun simulateText(text: String) {
        Actions(driver).sendKeys(text).perform()
    }

    fun focusEditor() {
        driver.switchTo().frame("wysiwygTextarea_ifr")
        dom.click("#tinymce")
        driver.switchTo().parentFrame()
    }

    fun setTwoColumnLayout() {
        dom.click("#page-layout-2")
        dom.click("#pagelayout2-toolbar > button:nth-child(2)")
    }

    fun goToDashboard() {
        navigateTo("dashboard.action")
    }

    fun clearEditorContent() {
        dom.insertTextTinyMce("")
    }

}