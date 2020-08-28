package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.E4Env
import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import de.scandio.e4.worker.util.RandomData
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class LivelyBlog_3_10_0 : BaseSeleniumTest() {

    private val log = LoggerFactory.getLogger(javaClass)

    val spaceKey = if (E4Env.PREPARATION_RUN) "LB${Date().time}" else "LB"

    init {
        if (E4Env.PREPARATION_RUN) {
            runWithDump {
                val restConfluence = restClient() as RestConfluence
                restConfluence.createSpace(spaceKey, spaceKey)
            }
        }
    }

    // LBCSRV-15: unset teaser images
    @Test
    fun LBCSRV_15() {
        runWithDump {
            val webConfluence = webConfluence() as WebConfluence
            val restConfluence = restClient() as RestConfluence
            val helper = LivelyBlogsSeleniumHelper(webConfluence)
            val dom = webConfluence.dom
            webConfluence.login()
            val timestamp = Date().time
            val title = "$spaceKey Blog Post ($timestamp)"
            val pageId = restConfluence.createPage(spaceKey, "Page with all the attachments ${Date().time}", "Page used for attachments")
            val images = helper.prepareImages("random-image-1.jpg")
            helper.uploadImages(pageId, images)
            webConfluence.navigateTo("pages/createblogpost.action?spaceKey=$spaceKey")
            dom.awaitElementPresent("#wysiwyg")
            dom.click("#wysiwyg")
            val content = "<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
            webConfluence.setPageTitleInEditor(title)
            webConfluence.focusEditor()
            dom.addTextTinyMce(content)
            // KEEP GOING
        }
    }

    @After
    fun after() {
        webClient().quit()
    }

    private fun webConfluence() : WebConfluence {
        return this.webClient!! as WebConfluence
    }

}