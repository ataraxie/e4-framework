package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class CreateSpaceAction(
        val spaceKey: String,
        val spaceName: String
) : Action {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val confluence = webClient as WebConfluence
        val dom = DomHelper(confluence)
        confluence.login()
        confluence.takeScreenshot("after-login")
        this.start = Date().time
        confluence.navigateTo("spaces/createspace-start.action")
        dom.awaitElementPresent("#create-space-form")
        confluence.takeScreenshot("createspace")
        dom.insertText("#create-space-form input[name='key']", spaceKey)
        dom.insertText("#create-space-form input[name='name']", spaceName)
        confluence.takeScreenshot("createspace-2")
        dom.awaitAttributeNotPresent("#create-space-form .aui-button[name='create']", "disabled")
        dom.await(1000)
        confluence.takeScreenshot("createspace-3")
        dom.click("#create-space-form .aui-button[name='create']")
        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]")
        confluence.takeScreenshot("createspace-final")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}