package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import de.scandio.e4.worker.util.RandomData
import org.apache.commons.io.FileUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.util.*

class LivelyBlog_3_9_1 : AbstractLivelyBlogTestSuite() {

    companion object {
        val spaceKey1 = "E4LB3911${Date().time}"
        val spaceName1 = "E4 Lively Blog 3.9.1 - 1"
        val spaceKey2 = "E4LB3912${Date().time}"
        val spaceName2 = "E4 Lively Blog 3.9.1 - 2"
        val macroId = "lively-blog-posts"

        @BeforeClass
        @JvmStatic internal fun beforeAll() {
            if (E4Env.PREPARATION_RUN) {
                runWithDump {
                    restConfluence.createSpace(spaceKey1, spaceName1)
                    restConfluence.createSpace(spaceKey2, spaceName2)
                }
            }
        }

        @AfterClass
        @JvmStatic internal fun afterAll() {
            webClient.quit()
        }
    }

    // LBCSRV-21: creates a blog post with a teaser image and makes sure the image is displayed correctly
    @Test
    fun LBCSRV_21() {
        runWithDump {
            webConfluence.login()
            val attachmentPageTitle = "Attachment Page ${Date().time}"
            val pageId = restConfluence.createPage(spaceKey1, attachmentPageTitle, "Attachment Page Content")

            val images = webConfluence.getFilesFromInputDir("random-image-0.jpg")
            if (images.isEmpty()) {
                val filename = "random-image-0.jpg"
                val path = "/images/$filename"
                val inputUrl = javaClass.getResource(path)
                val destUrl = "${webConfluence.inputDir}/$filename"
                FileUtils.copyURLToFile(inputUrl, File(destUrl))
            }

            webConfluence.navigateTo("pages/viewpageattachments.action?pageId=$pageId")
            dom.awaitElementPresent("#upload-files")
            dom.setFile("#file_0", images[0].absolutePath)
            dom.click("#edit")
            dom.awaitElementPresent(".filename[title='${images[0].name}']")

            webConfluence.createBlogpostKeepOpen(spaceKey1, "LB BlogPost")
            webConfluence.insertRandomImageFromPage(attachmentPageTitle)
            helper.setTeaserImage()
            webConfluence.savePageOrBlogPost()

            webConfluence.createPageKeepOpen(spaceKey1, "LB Macro Page")

            val paramMap = mapOf<String, String>()
            webConfluence.insertMacro(macroId, macroId, paramMap)
            webConfluence.savePageOrBlogPost()

            dom.awaitElementPresent(".field-image")
            dom.expectElementNotPresent(".field-image[style]")
        }
    }

    // LBCSRV-22 makes sure the @self macro parameter is applied (i.e. only blog posts from the current space are shown)
    @Test
    fun LBCSRV_22() {
        runWithDump {
            webConfluence.login()
            val blogpost1Title = webConfluence.createBlogpostAndSave(spaceKey1, "LB E4 Blog Post")
            val blogpost2Title = webConfluence.createBlogpostAndSave(spaceKey2, "LB E4 Blog Post")
            webConfluence.createPageKeepOpen(spaceKey1, "LB E4 Macro Page")
            val paramMap = mapOf<String, String>()
            webConfluence.insertMacro(macroId, macroId, paramMap)
            webConfluence.savePageOrBlogPost()
            dom.awaitElementPresent(".lively-blog-posts")
            dom.expectElementPresent(".lively-blog-posts a[title=\"$blogpost1Title\"]")
            dom.expectElementNotPresent(".lively-blog-posts a[title=\"$blogpost2Title\"]")
        }
    }

}