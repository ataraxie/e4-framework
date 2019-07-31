package de.scandio.e4.testpackages.livelytheme.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

/**
 * === FavPageToggleAction ===
 *
 * Lively Theme FavPageToggleAction action.
 *
 * Assumptions:
 * - NONE
 *
 * Procedure (SELENIUM):
 * - Navigate to a random page
 * - Toggle the favorite page button
 *
 * Result:
 * - Depending on previous state, page is now a favorite or not
 *
 * @author Felix Grund
 */
open class FavPageToggleAction : Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val restConfluence = restClient as RestConfluence
        val dom = webConfluence.dom
        val randomPageId = restConfluence.getRandomContentId()
        webConfluence.login()
        webConfluence.goToPage(randomPageId)
        this.start = Date().time
        dom.click("#page-favourite")
        dom.awaitMilliseconds(100)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}