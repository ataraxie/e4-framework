package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

/**
 * === UploadAllImages ===
 *
 * Lively Blogs UploadAllImages action.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space $spaceKey exists
 * - Page $pageTitle exists in space $spaceKey
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
        val pageId = restConfluence.getContentId(spaceKey, pageTitle)!!
        val images = webConfluence.prepareImages(this.filenameRegex)
        webConfluence.login()
        this.start = Date().time
        webConfluence.uploadImages(pageId, images)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}