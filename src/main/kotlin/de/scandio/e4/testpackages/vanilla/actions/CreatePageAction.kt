package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class CreatePageAction(
        val spaceKey: String,
        val pageTitle: String
) : Action {

    private val loremIpsum = "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."
    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.getDomHelper()
        webConfluence.login()
        webConfluence.takeScreenshot("after-login")
        this.start = Date().time
        webConfluence.navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        webConfluence.takeScreenshot("create-page-1")
        dom.click("#content-title-div")
        dom.insertText("#content-title", this.pageTitle)
        webConfluence.takeScreenshot("create-page-2")
        dom.click("#wysiwygTextarea_ifr")
        dom.insertTextTinyMce("<h1>Lorem Ipsum</h1><p>$loremIpsum</p>")
        webConfluence.takeScreenshot("create-page-3")
        dom.click("#rte-button-insert")
        dom.click("#rte-insert-macro")
        webConfluence.takeScreenshot("create-page-4")
        dom.awaitElementPresent("#macro-browser-dialog[aria-hidden]")
        webConfluence.takeScreenshot("create-page-5")
        dom.insertText("#macro-browser-search", "info")
        dom.awaitElementPresent("#macro-info")
        webConfluence.takeScreenshot("create-page-6")
        dom.click("#macro-info")
        dom.awaitElementPresent("#macro-param-title")
        dom.insertText("#macro-param-title", this.pageTitle)
        webConfluence.takeScreenshot("create-page-7")
        dom.click("#macro-details-page button.ok")
        dom.await(2000) // TODO: condition!
        webConfluence.takeScreenshot("create-page-8")
        dom.click("#rte-button-publish")
        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]")
        webConfluence.takeScreenshot("create-page-9")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}