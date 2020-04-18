package de.scandio.e4.testpackages.gitsnippets.actions

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.gitsnippets.GitSnippetsSeleniumHelper
import de.scandio.e4.worker.util.RandomData
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.util.*

class SetupGitSnippets: Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val helper = GitSnippetsSeleniumHelper(webConfluence, webClient.domHelper)
        val accessToken = helper.getAccessTokenFromEnvVar()
        webConfluence.login()
        this.start = Date().time
        helper.setAccessTokenInGitSnippetsConfig(accessToken)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }

}