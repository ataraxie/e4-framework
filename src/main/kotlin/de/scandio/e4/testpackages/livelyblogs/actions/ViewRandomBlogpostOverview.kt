package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.util.RandomData
import org.slf4j.LoggerFactory
import java.util.*

class ViewRandomBlogpostOverview: Action() {

    private var start: Long = 0
    private var end: Long = 0

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        webConfluence.login()
        this.start = Date().time
        webConfluence.navigateTo("plugins/lively/blog/all.action")
        dom.awaitElementPresent(".lively-blog-filter")
        val allItems = dom.findElements("li[data-filter]")
        val randomIndex = Random().nextInt(allItems.size - 1)
        val randomItem = allItems[randomIndex]
        log.info("Navigating on item {{}}", randomItem.getAttribute("data-filter"))
        dom.click(randomItem)
        dom.awaitMilliseconds(50)
        dom.awaitElementVisible(".blog-posts-container .lively-blog-posts")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }

}