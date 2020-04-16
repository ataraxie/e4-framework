package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.util.RandomData
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class LivelyBlogMacroTestSuite : BaseSeleniumTest() {

    private val log = LoggerFactory.getLogger(javaClass)

    private val SPACEKEY_1 = "E4LB1"
    private val SPACEKEY_2 = "E4LB2"
    private val MACRO_ID = "lively-blog-posts"

    init {
        val restConfluence = restClient() as RestConfluence
        try {
            restConfluence.createSpace(SPACEKEY_1, "E4 LB 1")
        } catch (e: Exception) {
            log.warn("Could not create space. Probably it already exists.")
        }
        try {
            restConfluence.createSpace(SPACEKEY_2, "E4 LB 2")
        } catch (e: Exception) {
            log.warn("Could not create space. Probably it already exists.")
        }
    }

    // LBCSRV-21: creates a blog post with a teaser image and makes sure the image is displayed correctly
    @Test
    fun testImagesNotTooWide() {
        try {
            val webConfluence = webConfluence()
            val dom = webConfluence.dom
            val restConfluence = restClient!! as RestConfluence
            webConfluence.login()
            val attachmentPageTitle = "Attachment Page ${Date().time}"
            val pageId = restConfluence.createPage(SPACEKEY_1, attachmentPageTitle, "Attachment Page Content")

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

            webConfluence.navigateTo("pages/createblogpost.action?spaceKey=$SPACEKEY_1")
            dom.awaitElementPresent("#wysiwyg")
            dom.click("#wysiwyg")
            val content = "Random Blog Post Content"
            val blogpost1Title = "$SPACEKEY_1 Blog Post (${Date().time})"
            webConfluence.setPageTitleInEditor(blogpost1Title)
            webConfluence.focusEditor()
            dom.addTextTinyMce(content)
            webConfluence.insertRandomImageFromPage(attachmentPageTitle)
            webConfluence.savePageOrBlogPost()
            val page1Title = "$SPACEKEY_1 Macro Page (${Date().time})"
            webConfluence.navigateTo("pages/createpage.action?spaceKey=$SPACEKEY_1")
            dom.awaitElementPresent("#wysiwyg")
            dom.click("#wysiwyg")
            webConfluence.setPageTitleInEditor(page1Title)
            val paramMap = mapOf<String, String>()
            webConfluence.insertMacro(MACRO_ID, MACRO_ID, paramMap)
            webConfluence.savePageOrBlogPost()
            dom.awaitElementPresent(".field-image")
            dom.expectElementNotPresent(".field-image[style]")
        } finally {
            dump()
            shot()
        }
    }

    // LBCSRV-22 makes sure the @self macro parameter is applied (i.e. only blog posts from the current space are shown)
    @Test
    fun testSpaceRestrictionIsApplied() {
        try {
            val webConfluence = webConfluence()
            val dom = webConfluence.dom
            webConfluence.login()
            val timestamp = Date().time
            val blogpost1Title = "$SPACEKEY_1 Blog Post ($timestamp)"
            val blogpost2Title = "$SPACEKEY_2 Blog Post ($timestamp)"
            createBlogPost(SPACEKEY_1, blogpost1Title)
            createBlogPost(SPACEKEY_2, blogpost2Title)
            val page1Title = "$SPACEKEY_1 Macro Page ($timestamp)"
            webConfluence.navigateTo("pages/createpage.action?spaceKey=$SPACEKEY_1")
            dom.awaitElementPresent("#wysiwyg")
            dom.click("#wysiwyg")
            webConfluence.setPageTitleInEditor(page1Title)
            val paramMap = mapOf<String, String>()
            webConfluence.insertMacro(MACRO_ID, MACRO_ID, paramMap)
            webConfluence.savePageOrBlogPost()
            dom.awaitElementPresent(".lively-blog-posts")
            dom.expectElementPresent(".lively-blog-posts a[title=\"$blogpost1Title\"]")
            dom.expectElementNotPresent(".lively-blog-posts a[title=\"$blogpost2Title\"]")
        } finally {
            dump()
            shot()
        }
    }

    fun createBlogPost(spaceKey: String, title: String) {
        val webConfluence = webConfluence()
        val dom = webConfluence.dom
        webConfluence.navigateTo("pages/createblogpost.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#wysiwyg")
        val content = "<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
        webConfluence.setPageTitleInEditor(title)
        webConfluence.focusEditor()
        dom.addTextTinyMce(content)
        webConfluence.savePageOrBlogPost()
    }

    @After
    fun after() {
        webClient().quit()
    }

    private fun webConfluence() : WebConfluence {
        return this.webClient!! as WebConfluence
    }

}