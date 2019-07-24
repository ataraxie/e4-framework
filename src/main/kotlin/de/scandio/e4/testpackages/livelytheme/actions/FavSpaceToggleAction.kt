package de.scandio.e4.testpackages.livelytheme.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*

/**
 * === FavSpaceToggleAction ===
 *
 * Lively Theme FavSpaceToggleAction action.
 *
 * Assumptions:
 * - NONE
 *
 * Procedure (SELENIUM):
 * - Navigate to a random page
 * - Open the sidebar if not open yet
 * - Toggle the favorite space button
 *
 * Result:
 * - Depending on previous state, space is now a favorite or not
 *
 * @author Felix Grund
 */
open class FavSpaceToggleAction : Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val restConfluence = restClient as RestConfluence
        val dom = webConfluence.dom
        val randomPageId = restConfluence.getRandomContentId()
        webConfluence.login()
        webConfluence.goToPage(randomPageId)
        this.start = Date().time
        if (dom.isElementPresent(".ia-fixed-sidebar.collapsed")) {
            dom.click(".space-tools-section .expand-collapse-trigger")
            dom.awaitMilliseconds(100)
        }

        if (dom.isElementPresent("#space-favourite-remove.space-favourite-hidden")) {
            dom.click("#space-favourite-add")
        } else {
            dom.click("#space-favourite-remove")
        }

        dom.awaitMilliseconds(100)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}