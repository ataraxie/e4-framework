package de.scandio.e4.testpackages.gitsnippets

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.worker.interfaces.WebClient
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory


class GitSnippetsSeleniumHelper(
        val webClient: WebClient
) {

    val webConfluence = webClient as WebConfluence
    val dom = webConfluence.dom

    private val ENV_VAR_KEY = "GIT_SNIPPETS_GITHUB_TOKEN"
    private val MACRO_ID = "live-snippet"

    private val log = LoggerFactory.getLogger(javaClass)

    fun getGithubAccessTokenFromEnvVar(): String {
        return System.getenv(ENV_VAR_KEY) ?: throw Exception("Access Token not found!")
    }

    fun goToGitSnippetsSettings(section: String = "bitbucket-server", additionalClickItem: String = "") {
        webConfluence.navigateTo("admin/plugins/git-snippets/settings.action", true)
        dom.click("li[data-settings-section=\"$section\"] a")
        if (StringUtils.isNotBlank(additionalClickItem)) {
            dom.awaitSeconds(1)
            dom.click("li[data-settings-section=\"$additionalClickItem\"] a")
        }
        dom.awaitSeconds(1) // FIXME: hard-coded interval! need to wait for event instead!
    }

    fun setGithubAccessTokenInAdmin(accessToken: String) {
        goToGitSnippetsSettings("github")
        dom.insertText("#github-access-token", accessToken, true)
        dom.click("#github-settings .aui-button.git-snippets-save-sources")
        webConfluence.awaitSuccessFlag()
    }

    fun createGitSnippetsMacroPageWaitTillRendered(spaceKey: String, pageTitle: String, macroParameters: Map<String, String>) {
        createGitSnippetsMacroPageKeepMacroBrowserOpen(spaceKey, pageTitle, macroParameters)
        webConfluence.saveMacroBrowser()
        saveAndWaitTillRendered()
    }

    fun createGitSnippetsMacroPageKeepMacroBrowserOpen(spaceKey: String, pageTitle: String, macroParameters: Map<String, String>) {
        webConfluence.navigateTo("pages/createpage.action?spaceKey=$spaceKey", true)
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#wysiwyg")
        webConfluence.setTitleInEditor(pageTitle)
        webConfluence.openMacroBrowser(MACRO_ID, MACRO_ID)
        dom.awaitMilliseconds(100)
        webConfluence.setMacroParameters(macroParameters)
        dom.awaitMilliseconds(300)
    }

    fun saveAndWaitTillRendered() {
        webConfluence.savePageOrBlogPost()
        dom.awaitElementPresent(".snippet-container")
    }

}