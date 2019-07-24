package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*


open class UploadAllImages (
        val spaceKey: String,
        val pageTitle: String,
        val filenameRegex: String
    ) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val restConfluence = restClient as RestConfluence
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        val pageId = restConfluence.getContentIdUseCache(spaceKey, pageTitle)
        val images = webConfluence.getFilesFromInputDir(this.filenameRegex)
        if (images.isEmpty()) {
            repeat(10) {
                val filename = "random-image-$it.jpg"
                val path = "/images/$filename"
                val inputUrl = javaClass.getResource(path)
                val destUrl = "${webConfluence.inputDir}/$filename"
                FileUtils.copyURLToFile(inputUrl, File(destUrl))
            }
        }
        webConfluence.login()
        this.start = Date().time
        for (image in images) {
            webConfluence.navigateTo("pages/viewpageattachments.action?pageId=$pageId")
            dom.awaitElementPresent("#upload-files")
            dom.setFile("#file_0", image.absolutePath)
            dom.click("#edit")
            dom.awaitElementPresent(".filename[title='${image.name}']")
            webConfluence.takeScreenshot("attachment-${image.name}")
        }

        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}