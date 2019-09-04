package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.util.RandomData
import org.slf4j.LoggerFactory
import java.util.*

/**
 * === SetupSetSpaceForFeaturedPosts ===
 *
 * Lively Blogs SetupSetSpaceForFeaturedPosts action.
 *
 * Assumptions:
 * - Lively Blogs app installed
 *
 * Procedure (SELENIUM):
 * - Go to Lively Blog settings in Confluence admin and set space with key "LB" as space for featured posts
 *
 * Result:
 * - Space with key "LB" is space for featured blog posts
 *
 * @author Felix Grund
 */
class SetupSetSpaceForFeaturedPosts: Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        webConfluence.login()
        this.start = Date().time
        webConfluence.navigateTo("admin/plugins/lively/blog/editsettings.action")
        webConfluence.dom.insertText("#spaces", "LB", true)
        webConfluence.dom.click("#confirm")
        webConfluence.dom.awaitElementPresent("span#spaces")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }

}