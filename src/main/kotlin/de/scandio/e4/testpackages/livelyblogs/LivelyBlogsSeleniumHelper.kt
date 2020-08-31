package de.scandio.e4.testpackages.livelyblogs

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.WebClient
import org.apache.commons.io.FileUtils
import java.io.File

class LivelyBlogsSeleniumHelper(
        protected val webClient: WebClient
) {

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
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
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
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        dom.click(".lively-blog-set-teaser")
    }

    fun setupFeaturedSpace(spaceKey: String) {
        val webConfluence = webClient as WebConfluence
        webConfluence.login()
        webConfluence.navigateTo("admin/plugins/lively/blog/editsettings.action")
        webConfluence.dom.insertText("#spaces", spaceKey, true)
        webConfluence.dom.click("#confirm")
        webConfluence.dom.awaitElementPresent("span#spaces")
    }

    fun goToBlogOverview() {
        val webConfluence = webClient as WebConfluence
        webConfluence.navigateTo("plugins/lively/blog/all.action")
        webConfluence.dom.awaitElementPresent(".lively-blog-filter")
    }


}