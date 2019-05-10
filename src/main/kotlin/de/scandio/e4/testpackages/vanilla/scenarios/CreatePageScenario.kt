package de.scandio.e4.testpackages.vanilla.scenarios

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class CreatePageScenario(
        val spaceKey: String,
        val pageTitle: String,
        val username: String = "admin",
        val password: String = "admin"
) : Scenario {

    private val loremIpsum = "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."
    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val confluence = webClient as WebConfluence
        val dom = DomHelper(confluence)
        confluence.login(this.username, this.password)
        confluence.takeScreenshot("after-login")
        confluence.goToSpaceHomepage(spaceKey)
        confluence.takeScreenshot("spacehomepage")
        this.start = Date().time
        dom.click("#quick-create-page-button")
        dom.awaitElementPresent("#wysiwyg")
        confluence.takeScreenshot("create-page-1")
        dom.click("#content-title-div")
        dom.insertText("#content-title", "TEST")
        confluence.takeScreenshot("create-page-2")
        dom.click("#wysiwygTextarea_ifr")
        dom.insertTextTinyMce("<h1>Lorem Ipsum</h1><p>$loremIpsum</p>")
        confluence.takeScreenshot("create-page-3")
        dom.click("#rte-button-insert")
        dom.click("#rte-insert-macro")
        confluence.takeScreenshot("create-page-4")
        dom.awaitElementPresent("#macro-browser-dialog[aria-hidden]")
        confluence.takeScreenshot("create-page-5")
        dom.insertText("#macro-browser-search", "info")
        dom.awaitElementPresent("#macro-info")
        confluence.takeScreenshot("create-page-6")
        dom.click("#macro-info")
        dom.awaitElementPresent("#macro-param-title")
        dom.insertText("#macro-param-title", this.pageTitle)
        confluence.takeScreenshot("create-page-7")
        dom.click("#macro-details-page button.ok")
        dom.await(2000) // TODO: condition!
        confluence.takeScreenshot("create-page-8")
        dom.click("#rte-button-publish")
        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]")
        confluence.takeScreenshot("create-page-9")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}