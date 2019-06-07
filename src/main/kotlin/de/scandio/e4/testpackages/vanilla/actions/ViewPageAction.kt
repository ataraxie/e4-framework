package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.WebClient
import java.net.URLEncoder
import java.util.*

/**
 * Confluence ViewPage action.
 *
 * Assumptions:
 * - Space with key $spaceKey
 * - Page with title $originPageTitle in space $spaceKey
 *
 * Procedure (SELENIUM):
 * - View page with title $originPageTitle in space $spaceKey
 * - Wait until page is loaded
 *
 * Result:
 * - Page with title "$branchName: $originPageTitle" is created
 *
 * @author Felix Grund
 */
open class ViewPageAction (
    val spaceKey: String,
    val pageTitle: String
    ) : Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val confluence = webClient as WebConfluence
        confluence.login()
        this.start = Date().time
        confluence.goToPage(spaceKey, pageTitle)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}