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
    private var startTime: Long = 0
    private var endTime: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        log.info("Executing ViewDelayPageScenario")
        val confluence = webClient as WebConfluence
        val dom = DomHelper(confluence)
        confluence.navigateTo("login.action")
        dom.awaitElementPresent("form[name='loginform']", 10)
        confluence.takeScreenshot("login")
        dom.insertText("#os_username", this.username)
        dom.insertText("#os_password", this.password)
        dom.click("#loginButton")
        dom.awaitElementPresent(".pagebody", 10)
        confluence.takeScreenshot("after-login")
        confluence.goToPage("E4", "Delay Macro Test")
        confluence.takeScreenshot("before-viewpage")
        dom.awaitElementPresent("#main-content", 10)
        confluence.takeScreenshot("viewpage")
        this.startTime = Date().time
        //webClient.goToLogin()
        webClient.takeScreenshot("login")
        this.endTime = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.endTime - this.startTime
    }


}
