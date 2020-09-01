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

    fun prepareImages(filenameRegex: String): List<File> {
        val webConfluence = webClient as WebConfluence
        val images = webConfluence.getFilesFromInputDir(filenameRegex)
        if (images.isEmpty()) {
            repeat(10) {
                val filename = "random-image-$it.jpg"
                val path = "/images/$filename"
                val inputUrl = javaClass.getResource(path)
                val destUrl = "${webConfluence.inputDir}/$filename"
                FileUtils.copyURLToFile(inputUrl, File(destUrl))
            }
        }
        return images
    }

    fun uploadImages(pageId: Long, images: List<File>) {
        for (image in images) {
            webConfluence.navigateTo("pages/viewpageattachments.action?pageId=$pageId")
            dom.awaitElementPresent("#upload-files")
            dom.setFile("#file_0", image.absolutePath)
            dom.click("#edit")
            dom.awaitElementPresent(".filename[title='${image.name}']")
            webConfluence.debugScreen("attachment-${image.name}")
        }
    }

    fun setTeaserImage() {
        dom.click(".lively-blog-set-teaser")
    }

    fun setupFeaturedSpace(spaceKey: String) {
        webConfluence.login()
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