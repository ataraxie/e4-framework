package de.scandio.e4.testpackages.gitsnippets.actions

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.gitsnippets.GitSnippetsSeleniumHelper
import org.slf4j.LoggerFactory
import java.util.*

class SetupGitSnippets: Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val helper = GitSnippetsSeleniumHelper(webConfluence)
        val accessToken = helper.getGithubAccessTokenFromEnvVar()
        webConfluence.login()
        this.start = Date().time
        helper.setGithubAccessTokenInAdmin(accessToken)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }

}