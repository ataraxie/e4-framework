package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.WebClient
import java.net.URLEncoder
import java.util.*

open class ViewPageAction (
    val spaceKey: String,
    val pageTitle: String
    ) : Action {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val confluence = webClient as WebConfluence
        val dom = DomHelper(confluence)
        val encodedPageTitle = URLEncoder.encode(pageTitle, "utf-8")
        confluence.login()

        this.start = Date().time
        confluence.navigateTo("display/$spaceKey/$encodedPageTitle")
        dom.awaitElementPresent("#main-content")
        this.end = Date().time
        confluence.takeScreenshot("view-page-$spaceKey-$encodedPageTitle")
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}