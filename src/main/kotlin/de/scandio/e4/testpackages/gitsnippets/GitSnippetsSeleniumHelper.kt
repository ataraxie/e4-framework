package de.scandio.e4.testpackages.gitsnippets

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.helpers.DomHelper
import org.slf4j.LoggerFactory


class GitSnippetsSeleniumHelper(
        val webConfluence: WebConfluence,
        val dom: DomHelper
) {

    private val ENV_VAR_KEY = "GIT_SNIPPETS_TOKEN"

    private val log = LoggerFactory.getLogger(javaClass)

    fun getAccessTokenFromEnvVar(): String {
        return System.getenv(ENV_VAR_KEY) ?: throw Exception("Access Token not found!")
    }

    fun setAccessTokenInGitSnippetsConfig(accessToken: String) {
        webConfluence.navigateTo("admin/plugins/git-snippets/settings.action")
        dom.awaitMilliseconds(3000) // FIXME: hard-coded interval! need to wait for event instead!
        dom.insertText("#githubPersonalAccessToken", accessToken, true)
        dom.click("#gitsnippets-settings-container .buttons .aui-button.submit")
        dom.awaitHasValue("#githubPersonalAccessToken", "****************************************")
    }

}