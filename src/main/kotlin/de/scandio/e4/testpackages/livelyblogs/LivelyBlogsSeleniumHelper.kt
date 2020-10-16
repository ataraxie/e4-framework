package de.scandio.e4.testpackages.livelyblogs

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.RandomData
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

class LivelyBlogsSeleniumHelper(
        protected val webClient: WebClient
) {

    val webConfluence = webClient as WebConfluence
    val dom = webConfluence.dom

    fun setTeaserImage() {
        dom.click(".lively-blog-set-teaser")
    }

    fun setupFeaturedSpace(spaceKey: String) {
        webConfluence.navigateTo("admin/plugins/lively/blog/editsettings.action")
        dom.insertText("#spaces", spaceKey, true)
        dom.click("#confirm")
        dom.awaitElementPresent("span#spaces")
    }

    fun goToBlogOverview() {
        webConfluence.navigateTo("plugins/lively/blog/all.action")
        dom.awaitElementPresent(".lively-blog-filter")
    }

    fun addLivelyBlogCategoryReturnId(categoryName: String): Int {
        webConfluence.navigateTo("admin/plugins/lively/blog/categories.action")
        dom.insertText("#categories-table input[name='name']", categoryName)
        dom.insertText("#categories-table input[name='labels']", categoryName)
        dom.click(".aui-restfultable-operations input[type='submit']")
        val tableRowSelector = ".aui-restfultable-row[data-name='$categoryName']"
        dom.awaitElementPresent(tableRowSelector)
        return Integer.parseInt(dom.findElement(tableRowSelector).getAttribute("data-id"))
    }

}