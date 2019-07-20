package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class ViewPageInfoAction(
        spaceKey: String,
        pageTitle: String
) : ViewPageAction(spaceKey, pageTitle) {

    override fun execute(webClient: WebClient, restClient: RestClient) {
        super.execute(webClient, restClient)
        val confluence = webClient as WebConfluence
        val dom = confluence.getDomHelper()
        this.start = Date().time
        dom.click("#action-menu-link")
        dom.awaitElementClickable("#view-page-info-link")
        dom.click("#view-page-info-link")
        dom.awaitElementPresent(".pageInfoTable")
        confluence.takeScreenshot("pageinfo")
        this.end = Date().time
    }

}