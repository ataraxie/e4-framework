package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.util.RandomData
import org.openqa.selenium.Keys
import org.openqa.selenium.interactions.Actions
import org.slf4j.LoggerFactory
import java.util.*

/**
 * === SearchBlogpostOverview ===
 *
 * Lively Blogs SearchBlogpostOverview action.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space with $spaceKey and $spaceName exists
 * - Blog posts with labels "label{1,5}" exiist
 *
 * Procedure (SELENIUM):
 * - Searches the Lively Blog overview page for blogposts in $spaceKey with a random label "label{1,5}"
 *
 * Result:
 * - Blog posts are shown according to criteria
 *
 * @author Felix Grund
 */
class SearchBlogpostOverview(
        val spaceKey: String,
        val spaceName: String
): Action() {

    private var start: Long = 0
    private var end: Long = 0

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        webConfluence.login()
        this.start = Date().time
        val label = "label${Random().nextInt(5) + 1}"
        webConfluence.navigateTo("plugins/lively/blog/all.action")
        dom.awaitElementClickable("#lbp-search-view-link")
        dom.click("#lbp-search-view-link")
        dom.click("#s2id_autogen1")
        Actions(dom.driver).sendKeys(spaceName).perform()
        dom.click(".select2-result-label[title='$spaceName']")
        dom.click("#s2id_autogen2")
        Actions(dom.driver).sendKeys(label).perform()
        dom.click(".select2-result:nth-child(1)")
        dom.click("#lbp-filter-submit")
        dom.awaitMilliseconds(50)
        dom.awaitElementVisible(".overview-container .lively-blog-posts")

        log.info("Search for label {{}}", label)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }

}