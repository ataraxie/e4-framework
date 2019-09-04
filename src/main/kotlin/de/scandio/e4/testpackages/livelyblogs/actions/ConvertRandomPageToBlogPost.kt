package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*

/**
 * === ConvertRandomPageToBlogPost ===
 *
 * Lively Blogs ConvertRandomPageToBlogPost action.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space with $spaceKey exists
 *
 * Procedure (SELENIUM):
 * - Take a random page and convert it to a blog post via page menu (without trashing page)
 *
 * Result:
 * - Random page has been converted to a blog post
 *
 * @author Felix Grund
 */
open class ConvertRandomPageToBlogPost(
        val spaceKey: String
) : Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        val restConfluence = restClient as RestConfluence
        webConfluence.login()
        val randomPageId = restConfluence.getRandomContentId(spaceKey)
        this.start = Date().time
        webConfluence.goToPage(randomPageId)
        dom.click("#action-menu-link")
        dom.click(".lively-blog-publish-as-blog-post-link")
        dom.click("#lively-blog-publish-as-blog-post-dialog button.submit")
        dom.awaitElementPresent("body.view-blog-post #main-content")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}