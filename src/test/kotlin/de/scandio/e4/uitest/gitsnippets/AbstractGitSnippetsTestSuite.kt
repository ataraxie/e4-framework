package de.scandio.e4.uitest.gitsnippets

import de.scandio.e4.E4Env
import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.gitsnippets.GitSnippetsRestHelper
import de.scandio.e4.testpackages.gitsnippets.GitSnippetsSeleniumHelper
import org.junit.AfterClass
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.slf4j.LoggerFactory
import java.util.*

@TestInstance(Lifecycle.PER_CLASS)
open class AbstractGitSnippetsTestSuite : BaseSeleniumTest() {

    companion object {
        @JvmStatic
        val log = LoggerFactory.getLogger(javaClass)

        @JvmStatic
        var webConfluence = webClient as WebConfluence
        @JvmStatic
        var restConfluence = restClient as RestConfluence
        @JvmStatic
        var webHelper = GitSnippetsSeleniumHelper(webConfluence)
        @JvmStatic
        var restHelper = GitSnippetsRestHelper(restConfluence)

        val SPACEKEY = if (E4Env.PREPARATION_RUN) "E4GS${Date().time}" else "GS"
        val SPACENAME = "E4 Git Snippets"

        @AfterClass
        @JvmStatic internal fun afterAll() {
            webClient.quit()
        }
    }
}