package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class CheckPageRestrictionsAction(
        spaceKey: String,
        pageTitle: String
) : ViewPageAction(spaceKey, pageTitle) {

    override fun execute(webClient: WebClient, restClient: RestClient) {
        super.execute(webClient, restClient)
        val confluence = webClient as WebConfluence
        val dom = DomHelper(confluence.driver)
        this.start = Date().time
        dom.click("#content-metadata-page-restrictions")
        dom.awaitElementPresent("#update-page-restrictions-dialog")
        confluence.takeScreenshot("restrictions")
        this.end = Date().time
    }

}