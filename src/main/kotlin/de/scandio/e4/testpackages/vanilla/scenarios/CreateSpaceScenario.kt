package de.scandio.e4.testpackages.vanilla.scenarios

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class CreateSpaceScenario(
        val spaceKey: String,
        val spaceName: String,
        val username: String = "admin",
        val password: String = "admin"
) : Scenario {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val confluence = webClient as WebConfluence
        val dom = DomHelper(confluence)
        confluence.login(this.username, this.password)
        confluence.takeScreenshot("after-login")
        confluence.goToDashboard()
        this.start = Date().time
        dom.clickCreateSpace()
        confluence.takeScreenshot("createspace")
        dom.awaitElementPresent("li.template.selected")
        confluence.takeScreenshot("createspace-2")
        dom.click("#create-dialog .create-dialog-create-button")
        confluence.takeScreenshot("createspace-3")
        dom.awaitElementPresent("form.common-space-form")
        confluence.takeScreenshot("createspace-4")
        dom.insertText("form.common-space-form input[name='name']", spaceName)
        dom.await(1000) // TODO: condition
        dom.clearText("form.common-space-form input[name='spaceKey']")
        confluence.takeScreenshot("createspace-5")
        dom.insertText("form.common-space-form input[name='spaceKey']", spaceKey)
        confluence.takeScreenshot("createspace-6")
        val createButtonSelector = "#create-dialog .create-dialog-commonPage + .dialog-button-panel .create-dialog-create-button"
        dom.awaitAttributeNotPresent(createButtonSelector, "disabled")
        confluence.takeScreenshot("createspace-7")
        dom.click(createButtonSelector) // TODO: duplicate but it doesn't work without somehow
        dom.await(1000) // TODO: condition
        dom.click(createButtonSelector)
        confluence.takeScreenshot("createspace-8")
        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]", 20)
        confluence.takeScreenshot("createspace-final")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}