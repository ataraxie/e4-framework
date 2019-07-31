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
 * === SetupLivelyBlogCategories ===
 *
 * Lively Blogs SetupLivelyBlogCategories action.
 *
 * Assumptions:
 * - Lively Blogs app installed
 *
 * Procedure (SELENIUM):
 * - Admin goes to Lively Blog Posts Categories page and creates category{1,5} and label{1,5}
 *
 * Result:
 * - Categories category{1,5} and Labels label{1,5} are added in Lively Blogs categories administration
 *
 * @author Felix Grund
 */
class SetupLivelyBlogCategories: Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    val colors = arrayListOf("#0747A6","#008DA6","#006644","#FF8B00","#BF2600")

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        webConfluence.login()
        this.start = Date().time
        webConfluence.navigateTo("admin/plugins/lively/blog/categories.action")
        repeat(5) {
            val categoryName = "Category${it+1}"
            dom.insertText("#categories-table input[name='name']", categoryName)
            dom.insertText("#categories-table input[name='color']", colors[it])
            dom.insertText("#categories-table input[name='labels']", "label${it+1}")
            dom.click(".aui-restfultable-operations input[type='submit']")
            dom.awaitElementPresent(".aui-restfultable-row[data-name='$categoryName']")
        }

        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }

}