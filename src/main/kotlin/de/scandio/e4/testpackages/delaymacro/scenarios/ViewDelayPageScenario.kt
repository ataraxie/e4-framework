package de.scandio.e4.testpackages.delaymacro.scenarios

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*

class ViewDelayPageScenario(
        val username: String,
        val password: String
) : Scenario {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(webClient: WebClient, restClient: RestClient) {
        log.info("Executing ViewDelayPageScenario")
        val confluence = webClient as WebConfluence
        val dom = DomHelper(confluence)
        confluence.navigateTo("login.action")
        dom.awaitElementPresent("form[name='loginform']", 10)
        confluence.takeScreenshot("login")
        dom.insertText("#os_username", username)
        dom.insertText("#os_password", password)
        dom.click("#loginButton")
        dom.awaitElementPresent(".pagebody", 10)
        confluence.takeScreenshot("after-login")
        confluence.navigateToPage("E4", "Delay Macro Test")
        confluence.takeScreenshot("before-viewpage")
        dom.awaitElementPresent("#main-content", 10)
        confluence.takeScreenshot("viewpage")
    }

}