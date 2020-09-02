package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import de.scandio.e4.worker.util.RandomData
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.io.File
import java.util.*

@TestInstance(Lifecycle.PER_CLASS) // FIXME: DOES NOT WORK (ONE INSTANCE PER TEST METHOD IS CREATED)
class LivelyBlog_3_9_1 : AbstractLivelyBlogTestSuite() {

    private val spaceKey1 = "E4LB3911${Date().time}"
    private val spaceName1 = "E4 Lively Blog 3.9.1 - 1"
    private val spaceKey2 = "E4LB3912${Date().time}"
    private val spaceName2 = "E4 Lively Blog 3.9.1 - 2"
    private val macroId = "lively-blog-posts"

    init {
        if (E4Env.PREPARATION_RUN) {
            runWithDump {
                restConfluence.createSpace(spaceKey1, spaceName1)
                restConfluence.createSpace(spaceKey2, spaceName2)
            }
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

            webConfluence.navigateTo("pages/createblogpost.action?spaceKey=$spaceKey1")
            dom.awaitElementPresent("#wysiwyg")
            dom.click("#wysiwyg")
            val content = "Random Blog Post Content"
            val blogpost1Title = "$spaceKey1 Blog Post (${Date().time})"
            val helper = LivelyBlogsSeleniumHelper(webConfluence)
            webConfluence.setPageTitleInEditor(blogpost1Title)
            webConfluence.focusAndUnfocusEditor()
            dom.addTextTinyMce(content)
            webConfluence.insertRandomImageFromPage(attachmentPageTitle)
            helper.setTeaserImage()
            webConfluence.savePageOrBlogPost()
            val page1Title = "$spaceKey1 Macro Page (${Date().time})"
            webConfluence.navigateTo("pages/createpage.action?spaceKey=$spaceKey1")
            dom.awaitElementPresent("#wysiwyg")
            dom.click("#wysiwyg")
            webConfluence.setPageTitleInEditor(page1Title)
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
            val timestamp = Date().time
            val blogpost1Title = "$spaceKey1 Blog Post ($timestamp)"
            val blogpost2Title = "$spaceKey2 Blog Post ($timestamp)"
            createBlogPost(spaceKey1, blogpost1Title)
            createBlogPost(spaceKey2, blogpost2Title)
            val page1Title = "$spaceKey1 Macro Page ($timestamp)"
            webConfluence.navigateTo("pages/createpage.action?spaceKey=$spaceKey1")
            dom.awaitElementPresent("#wysiwyg")
            dom.click("#wysiwyg")
            webConfluence.setPageTitleInEditor(page1Title)
            val paramMap = mapOf<String, String>()
            webConfluence.insertMacro(macroId, macroId, paramMap)
            webConfluence.savePageOrBlogPost()
            dom.awaitElementPresent(".lively-blog-posts")
            dom.expectElementPresent(".lively-blog-posts a[title=\"$blogpost1Title\"]")
            dom.expectElementNotPresent(".lively-blog-posts a[title=\"$blogpost2Title\"]")
        }
    }

    fun createBlogPost(spaceKey: String, title: String) {
        runWithDump {
            webConfluence.navigateTo("pages/createblogpost.action?spaceKey=$spaceKey")
            dom.awaitElementPresent("#wysiwyg")
            dom.click("#wysiwyg")
            val content = "<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
            webConfluence.setPageTitleInEditor(title)
            webConfluence.focusAndUnfocusEditor()
            dom.addTextTinyMce(content)
            webConfluence.savePageOrBlogPost()
        }
    }

    @After
    fun after() {
        webClient.quit()
    }
}