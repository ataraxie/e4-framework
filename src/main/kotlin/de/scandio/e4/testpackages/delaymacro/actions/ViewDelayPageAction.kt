package de.scandio.e4.testpackages.delaymacro.actions

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import java.util.*

class ViewDelayPageAction(
        val username: String = "admin",
        val password: String = "admin"
) : Action {

    private val log = LoggerFactory.getLogger(javaClass)
    private var startTime: Long = 0
    private var endTime: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        log.info("Executing ViewDelayPageAction")
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
        this.startTime = Date().time
        confluence.navigateTo("display/E4/${URLEncoder.encode("Delay Macro Test", "utf-8")}")
        dom.awaitElementPresent("#main-content")
        this.endTime = Date().time
        confluence.takeScreenshot("viewpage")
    }

    override fun getTimeTaken(): Long {
        return this.endTime - this.startTime
    }


}
