package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import de.scandio.e4.worker.util.RandomData
import org.slf4j.LoggerFactory
import java.util.*

/**
 * === ViewRandomBlogpostOverview ===
 *
 * Lively Blogs ViewRandomBlogpostOverview action.
 *
 * Assumptions:
 * - Lively Blogs app installed
 *
 * Procedure (SELENIUM):
 * - Check if there are images with names "random-image-{1,9}" in E4 INPUT DIR
 * - If NOT: copy images from /images in the e4.jar to E4 INPUT DIR
 * - Upload 9 images with names "random-image-{1,9}" to page $pageTitle in space $spaceKey
 *
 * Result:
 * - Images with names "random-image-{1,9}" are attached to page $pageTitle in space $spaceKey
 *
 * @author Felix Grund
 */
class ViewRandomBlogpostOverview: Action() {

    private var start: Long = 0
    private var end: Long = 0

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        val helper = LivelyBlogsSeleniumHelper(webConfluence)
        webConfluence.login()
        this.start = Date().time
        helper.goToBlogOverview()
        val allItems = dom.findElements("li[data-filter]")
        val randomIndex = Random().nextInt(allItems.size - 1)
        val randomItem = allItems[randomIndex]
        log.info("Navigating on item {{}}", randomItem.getAttribute("data-filter"))
        dom.click(randomItem)
        dom.awaitMilliseconds(50)
        dom.awaitElementVisible(".overview-container .lively-blog-posts")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }

}