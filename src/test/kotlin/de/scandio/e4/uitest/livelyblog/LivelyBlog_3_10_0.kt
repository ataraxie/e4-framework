package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.E4Env
import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import de.scandio.e4.worker.util.RandomData
import org.junit.After
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class LivelyBlog_3_10_0 : BaseSeleniumTest() {

    private val log = LoggerFactory.getLogger(javaClass)

    val spaceKey = if (E4Env.PREPARATION_RUN) "LB${Date().time}" else "LB"

    init {
        if (E4Env.PREPARATION_RUN) {
            runWithDump {
                val restConfluence = restClient() as RestConfluence
                val webConfluence = webConfluence() as WebConfluence
                val helper = LivelyBlogsSeleniumHelper(webConfluence)
                restConfluence.createSpace(spaceKey, spaceKey)
                helper.setupFeaturedSpace(spaceKey)
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
            val blogpostTitle = "$spaceKey Blog Post ($timestamp)"
            // First, upload some attachments to some new random page such that we can create teaser images
            val pageId = restConfluence.createPage(spaceKey, "Page with all the attachments ${Date().time}", "Page used for attachments")
            val images = helper.prepareImages("random-image-1.jpg")
            helper.uploadImages(pageId, images)
            webConfluence.navigateTo("pages/createblogpost.action?spaceKey=$spaceKey") //Create a new blog post
            dom.awaitElementPresent("#wysiwyg")
            dom.click("#wysiwyg")
            val content = "<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
            webConfluence.setPageTitleInEditor(blogpostTitle)
            webConfluence.focusAndUnfocusEditor()
            dom.addTextTinyMce(content)
            webConfluence.insertRandomImageFromPage("Page") //Insert an image from your local hard drive into the blog post
            expectButtonNotActive()
            clickTeaserButton()
            expectButtonActive()
            unfocusImage()
            clickImage()
            expectButtonActive()
            clickTeaserButton()
            expectButtonNotActive()
            unfocusImage()
            clickImage()
            expectButtonNotActive()
            clickTeaserButton()
            expectButtonActive()
            webConfluence.savePageOrBlogPost()
            dom.awaitSeconds(5) // Give the index a few seconds
            webConfluence.goToDashboard()
            dom.awaitSeconds(3)
            expectTeaserIsDisplayed(blogpostTitle)
            dom.click(".post[alt='${blogpostTitle}'] img[alt='${blogpostTitle}']")
            dom.awaitElementPresent("#main-content")
            webConfluence.goToEditPage()
            clickImage()
            expectButtonActive()
            clickTeaserButton()
            expectButtonNotActive()
            webConfluence.savePageOrBlogPost()
            dom.awaitSeconds(5) // Again, give it a few seconds
            webConfluence.goToDashboard()
            dom.awaitSeconds(3)
            expectTeaserIsNotDisplayed(blogpostTitle)
        }
    }

    fun expectTeaserIsDisplayed(blogpostTitle: String) {
        awaitBlogpostPresentInList(blogpostTitle)
        webConfluence().dom.expectElementPresent(".post[alt='${blogpostTitle}'] img[alt='${blogpostTitle}']")
    }

    fun expectTeaserIsNotDisplayed(blogpostTitle: String) {
        awaitBlogpostPresentInList(blogpostTitle)
        webConfluence().dom.expectElementNotPresent(".post[alt='${blogpostTitle}'] img[alt='${blogpostTitle}']")
    }

    fun awaitBlogpostPresentInList(blogpostTitle: String) {
        webConfluence().dom.awaitElementPresent(".post[alt='${blogpostTitle}']")
    }

    fun expectButtonActive() {
        webConfluence().unfocusEditor()
        webConfluence().dom.expectElementPresent(".lively-blog-set-teaser.active")
    }

    fun expectButtonNotActive() {
        webConfluence().unfocusEditor()
        webConfluence().dom.expectElementNotPresent(".lively-blog-set-teaser.active")
    }

    fun clickTeaserButton() {
        webConfluence().unfocusEditor()
        webConfluence().dom.click(".lively-blog-set-teaser")
        webConfluence().dom.awaitSeconds(1)
    }

    fun unfocusImage() {
        webConfluence().unfocusEditor()
        webConfluence().dom.click("#content-title-div")
        webConfluence().dom.awaitSeconds(1)
    }

    fun clickImage() {
        webConfluence().focusEditor()
        webConfluence().dom.click(".confluence-embedded-image")
        webConfluence().dom.awaitSeconds(1)
    }

    @After
    fun after() {
        webClient().quit()
    }

    private fun webConfluence() : WebConfluence {
        return this.webClient!! as WebConfluence
    }

}