package de.scandio.e4.clients.web

import de.scandio.e4.worker.util.RandomData
import de.scandio.e4.worker.util.WorkerUtils
import org.apache.commons.io.FileUtils
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class WebConfluence(
        driver: WebDriver,
        base: URI,
        inputDir: String,
        outputDir: String,
        username: String,
        password: String
): WebAtlassian(driver, base, inputDir, outputDir, username, password) {

    companion object {
        val DUMMY_PAGE_CONTENT = "<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p><p></p>"
    }

    override fun login() {
        // Do the following if you want to do it only initially when the browser is opened
        // if (driver.currentUrl.equals("about:blank") || driver.currentUrl.equals("data:,")) { // only login once!
        navigateTo("login.action")
        dom.awaitElementPresent("form[name='loginform'], .login-section p.last, #main-content", 40)
        if (dom.isElementPresent("form[name='loginform']")) {
            dom.insertText("#os_username", this.username)
            dom.insertText("#os_password", this.password)
            dom.click("#loginButton")
            dom.awaitElementPresent(".pagebody", 40)
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

    fun goToPage(pageId: Long, loadedSelector: String = "#main-content") {
        navigateTo("pages/viewpage.action?pageId=$pageId")
        dom.awaitElementPresent(loadedSelector)
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

    fun goToBlogpost(spaceKey: String, blogpostTitle: String, blogpostCreationDate: String) {
        val encodedTitle = URLEncoder.encode(blogpostTitle, "UTF-8")
        navigateTo("display/$spaceKey/$blogpostCreationDate/$encodedTitle")
    }

    fun goToCreatePage(spaceKey: String) {
        navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        awaitEditorLoaded()
    }

    fun goToCreateBlogpost(spaceKey: String) {
        navigateTo("pages/createblogpost.action?spaceKey=$spaceKey")
        awaitEditorLoaded()
    }

    fun openMacroBrowser(macroId: String, macroSearchTerm: String) {
        log.debug("Inserting macro {{}}", macroId)
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

    fun openInsertImageDialog() {
        driver.switchTo().frame("wysiwygTextarea_ifr")
        dom.click("#tinymce")
        driver.switchTo().parentFrame()
        dom.click("#confluence-insert-files")
    }

    fun saveMacroBrowser() {
        dom.click("#macro-details-page button.ok", 5)
        debugScreen("saveMacroBrowser-1")
        dom.awaitElementClickable("#rte-button-publish")
        dom.awaitMilliseconds(50)
    }

    fun setMacroParameters(macroParameters: Map<String, String> = emptyMap()) {
        for ((paramKey, paramValue) in macroParameters) {
            val selector = "#macro-browser-dialog #macro-param-$paramKey"
            val elem = dom.findElement(selector)
            if ("select" == elem.tagName) {
                dom.setSelectedOption(selector, paramValue)
            } else if (elem.getAttribute("type") == "checkbox") {
                if (elem.isSelected && paramValue == "false" || !elem.isSelected && paramValue == "true") {
                    dom.click(elem)
                }
            }
            else {
                dom.insertText(selector, paramValue, true)
            }
        }
    }

    fun insertMacro(macroId: String, macroSearchTerm: String, macroParameters: Map<String, String> = emptyMap()) {
        openMacroBrowser(macroId, macroSearchTerm)
        debugScreen("after-openMacroBrowser")
        dom.awaitMilliseconds(100)
        setMacroParameters(macroParameters)
        debugScreen("after-setParams")
        saveMacroBrowser()
    }

    fun insertMarkdown(markdown: String) {
        driver.switchTo().frame("wysiwygTextarea_ifr")
        dom.click("#tinymce")
        driver.switchTo().parentFrame()
        dom.click("#rte-button-insert")
        dom.click("#rte-insert-wikimarkup")
        dom.setSelectedOption("#wiki-parser-selection-tool", "MARKDOWN")
        dom.insertText("#insert-wiki-textarea", markdown)
        dom.click("#insert-wiki-markup-dialog .button-panel-button")
        dom.awaitElementClickable("#rte-button-publish")
        dom.awaitMilliseconds(50)
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

    fun savePageOrBlogPost() {
        dom.removeElementWithJQuery(".aui-blanket")
        dom.click("#rte-button-publish")
        dom.awaitElementPresent("#main-content")
    }

    private fun getPageId(): Number {
        return dom.executeScript("AJS.Meta.get(\"page-id\")").toString().toInt()
    }

    fun openCreatePageEditorByQuickCreate() {
        dom.removeElementWithJQuery(".aui-blanket")
        dom.click("#quick-create-page-button")
        awaitEditorLoaded()
    }

    fun createPageAndSave(spaceKey: String, title: String, appendTimestamp: Boolean = true,
                          content: String = DUMMY_PAGE_CONTENT): String {
        val actualTitle = createPageKeepOpen(spaceKey, title, appendTimestamp, content)
        savePageOrBlogPost()
        return actualTitle
    }

    fun createPageAndSaveRandomSpace(title: String, appendTimestamp: Boolean = true,
                                     content: String = DUMMY_PAGE_CONTENT): String {
        openCreatePageEditorByQuickCreate()
        return fillPageOrBlogpostAndSave(title, appendTimestamp, content)
    }

    fun createPageKeepOpen(spaceKey: String, title: String, appendTimestamp: Boolean = true,
                           content: String = DUMMY_PAGE_CONTENT): String {
        goToCreatePage(spaceKey)
        return fillPageOrBlogpost(title, appendTimestamp, content)
    }

    fun fillPageOrBlogpostAndSave(title: String, appendTimestamp: Boolean = true,
                                  content: String = DUMMY_PAGE_CONTENT): String {
        val actualTitle = fillPageOrBlogpost(title, appendTimestamp, content)
        savePageOrBlogPost()
        return actualTitle
    }

    fun createBlogpostKeepOpen(spaceKey: String, title: String, appendTimestamp: Boolean = true,
                               content: String = DUMMY_PAGE_CONTENT): String {
        goToCreateBlogpost(spaceKey)
        return fillPageOrBlogpost(title, appendTimestamp, content)
    }

    fun createBlogpostAndSave(spaceKey: String, title: String, appendTimestamp: Boolean = true): String {
        val actualTitle = createBlogpostKeepOpen(spaceKey, title, appendTimestamp)
        savePageOrBlogPost()
        return actualTitle
    }

    fun setTitleInEditor(title: String, appendTimestamp: Boolean = true): String {
        var actualTitle = title
        if (appendTimestamp) {
            actualTitle += " (${Date().time})"
        }
        dom.click("#content-title-div", 40)
        dom.insertText("#content-title", actualTitle)
        return actualTitle
    }

    private fun fillPageOrBlogpost(title: String, appendTimestamp: Boolean = true,
                                   content: String = DUMMY_PAGE_CONTENT): String {
        if (dom.isElementPresent("#closeDisDialog")) {
            dom.click("#closeDisDialog")
            dom.awaitMilliseconds(100)
        }
        val titleWithTimestamp = setTitleInEditor(title, appendTimestamp)
        focusAndUnfocusEditor()
        addContentInEditor(content)
        return titleWithTimestamp
    }

    fun addContentInEditor(content: String) {
        dom.addTextTinyMce(content)
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
        dom.awaitSeconds(20)
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
        savePageOrBlogPost()
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
        val macroBodySelector = ".wysiwyg-macro[data-macro-name=\"$macroId\"] .wysiwyg-macro-body"
        dom.executeScript("$('#wysiwygTextarea_ifr').contents().find('$macroBodySelector').html('$htmlBody')")
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

    fun focusAndUnfocusEditor() {
        focusEditor()
        unfocusEditor()
    }

    fun focusEditor() {
        driver.switchTo().frame("wysiwygTextarea_ifr")
        dom.click("#tinymce")
    }

    fun unfocusEditor() {
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

    fun setSelect2Option(selector: String, value: String) {
        dom.executeScript("$('$selector').val('$value').trigger('change')")
        dom.awaitMilliseconds(50)
    }

    fun insertRandomImageFromPage(attachmentPageTitleSearch: String) {
        log.debug("Inserting random image from page {{}}", attachmentPageTitleSearch)
        openInsertImageDialog()
        debugScreen("insertRandomImageFromPage-1")
        dom.click("#insert-image-dialog .page-menu-item:nth-child(3)")
        dom.insertText("#search-image-form .search-image-text", attachmentPageTitleSearch)
        debugScreen("insertRandomImageFromPage-2")
        dom.click("#search-image-form .search-button")
        dom.awaitElementVisible("#searched-images .attached-file")
        debugScreen("insertRandomImageFromPage-3")
        val allImages = dom.findElements("#searched-images .attached-file")
        var randIndex = 0
        if (allImages.size > 1) {
            randIndex = Random().nextInt(allImages.size - 1)
        }
        dom.click(allImages[randIndex])
        debugScreen("insertRandomImageFromPage-4")
        dom.click("#insert-image-dialog button.insert")
    }

    fun enterReadOnlyMode() {
        navigateTo("admin/maintenance/edit.action")
        dom.click("#readOnlyModeEnabled:not(:checked)")
        dom.click("#confirm")
    }

    fun exitReadOnlyMode() {
        navigateTo("admin/maintenance/edit.action")
        dom.click("#readOnlyModeEnabled:checked")
        dom.click("#confirm")
    }

    fun getRandomMarkdownContent(): String {
        var markdownFiles = getFilesFromInputDir("random-markdown.*\\.md")
        if (markdownFiles.isEmpty()) {
            repeat(10) {
                val filename = "random-markdown-$it.md"
                val path = "/markdown/$filename"
                val inputUrl = javaClass.getResource(path)
                val destUrl = "${inputDir}/$filename"
                FileUtils.copyURLToFile(inputUrl, File(destUrl))
            }
        }
        markdownFiles = getFilesFromInputDir("random-markdown.*\\.md")
        val randInt = WorkerUtils.getRandomItem(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
        val markdownFile = markdownFiles[randInt]
        return FileUtils.readFileToString(markdownFile, StandardCharsets.UTF_8);
    }

    fun likeOrUnlikePageOrBlogpost() {
        dom.click("#likes-section .like-button")
    }

    fun focusMacroBrowserPreviewFrame() {
        driver.switchTo().frame("macro-preview-iframe")
    }

    fun awaitEditPageLoaded() {
        dom.awaitElementClickable("#content-title-div", 40)
    }

    fun awaitEditorLoaded() {
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#wysiwyg")
    }

}