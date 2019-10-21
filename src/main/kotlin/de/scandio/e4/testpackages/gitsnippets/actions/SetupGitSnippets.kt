package de.scandio.e4.testpackages.gitsnippets.actions

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.util.RandomData
import org.slf4j.LoggerFactory
import java.util.*

class SetupGitSnippets: Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        val accessToken = "60578a45881a97098af808b9198830383b799b2d"
        webConfluence.login()
        this.start = Date().time
        webConfluence.navigateTo("admin/plugins/git-snippets/settings.action")
        dom.awaitMilliseconds(3000)
        dom.insertText("#githubPersonalAccessToken", accessToken, true)
        dom.click(".settings .buttons .aui-button.submit")
        dom.awaitHasValue("#githubPersonalAccessToken", "****************************************")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }

}