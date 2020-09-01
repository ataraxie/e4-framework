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

    // NOTE: If you re-use the space (i.e. no PREPARATION_RUN flag set, you'll have to make sure your space is set as
    // the featured space in the global LB settings.
    val spaceKey = if (E4Env.PREPARATION_RUN) "LB${Date().time}" else "LB"

    init {
        val webConfluence = webConfluence()
        val helper = LivelyBlogsSeleniumHelper(webConfluence)
        if (E4Env.PREPARATION_RUN) {
            runWithDump {
                val restConfluence = restClient() as RestConfluence
                restConfluence.createSpace(spaceKey, spaceKey)
                helper.setupFeaturedSpace(spaceKey)
                // First, upload some attachments to some new random page such that we can create teaser images
                val pageId = restConfluence.createPage(spaceKey, "Page with all the attachments ${Date().time}", "Page used for attachments")
                val images = helper.prepareImages("random-image-1.jpg")
                helper.uploadImages(pageId, images)
            }
        }
    }

    // LBCSRV-32: teaser warning
    @Test
    fun LBCSRV_32() {
        runWithDump {
            val webConfluence = webConfluence()
            val helper = LivelyBlogsSeleniumHelper(webConfluence)
            val dom = webConfluence.dom
            webConfluence.login()
            val timestamp = Date().time
            val blogpostTitle = "$spaceKey Blog Post ($timestamp)"
            startCreateBlogpostKeepOpen(blogpostTitle)
            dom.expectElementPresent(".info-no-teaser.active")
            webConfluence.insertRandomImageFromPage("Page")
            clickImage()
            clickTeaserButton()
            dom.expectElementNotPresent(".info-no-teaser.active")
            clickImage()
            clickTeaserButton()
            dom.expectElementPresent(".info-no-teaser.active")
            clickImage()
            clickProperties()
            dom.click("button#lively-blog-set-teaser") // FIXME: DUPLICATE ID (see LBCSRV-44)
            dom.click("input#lively-blog-set-teaser")
            dom.click("#image-properties-dialog .button-panel-submit-button")
            dom.awaitSeconds(1)
            dom.expectElementNotPresent(".info-no-teaser.active")
            clickImage()
            clickTeaserButton()
            dom.expectElementPresent(".info-no-teaser.active")
        }
    }

    // LBCSRV-31: direct likes
    @Test
    fun LBCSRV_31() {
        runWithDump {
            val webConfluence = webConfluence()
            val helper = LivelyBlogsSeleniumHelper(webConfluence)
            val dom = webConfluence.dom
            webConfluence.login()
            val timestamp = Date().time
            val blogpostTitle = "$spaceKey Blog Post ($timestamp)"
            startCreateBlogpostKeepOpen(blogpostTitle) // create a blog post
            webConfluence.savePageOrBlogPost() // save it
            dom.awaitSeconds(3) // give index a bit
            helper.goToBlogOverview() // go to blog overview
            awaitBlogpostPresentInList(blogpostTitle)
            expectNoLikesButton(blogpostTitle) // there should be no likes button for the blog post
            webConfluence.goToDashboard() // go to the dashboard
            awaitBlogpostPresentInList(blogpostTitle)
            expectNoLikesButton(blogpostTitle) // there should be no likes button for the blog post
            dom.click(".post[alt='${blogpostTitle}'] .title a") // go to blogpost
            webConfluence.likeOrUnlikePageOrBlogpost() // click like button
            helper.goToBlogOverview() // go back to blog overview
            awaitBlogpostPresentInList(blogpostTitle)
            expectLikesButton(blogpostTitle) // Likes should be there
            webConfluence.goToDashboard() // go to dashboard
            awaitBlogpostPresentInList(blogpostTitle)
            expectLikesButton(blogpostTitle) // Likes should be there
            likeOrUnlikeInList(blogpostTitle) // click Like button
            awaitZeroLikes(blogpostTitle) // should have 0 Likes
            likeOrUnlikeInList(blogpostTitle) // click again
            awaitOneLike(blogpostTitle) // should have one like
        }
    }

    // LBCSRV-15: unset teaser images
    @Test
    fun LBCSRV_15() {
        runWithDump {
            val webConfluence = webConfluence()
            val dom = webConfluence.dom
            webConfluence.login()
            dom.awaitSeconds(2)
            val blogpostTitle = "$spaceKey Blog Post (${Date().time})"
            startCreateBlogpostKeepOpen(blogpostTitle)
            webConfluence.insertRandomImageFromPage("Page")
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

    fun expectNoLikesButton(blogpostTitle: String) {
        webConfluence().dom.expectElementNotPresent(".post[alt='${blogpostTitle}'] .field-interaction .likes")
    }

    fun expectLikesButton(blogpostTitle: String) {
        webConfluence().dom.expectElementPresent(".post[alt='${blogpostTitle}'] .field-interaction .likes")
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

    fun clickProperties() {
        webConfluence().unfocusEditor()
        webConfluence().dom.click(".aui-button.image-properties")
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

    fun likeOrUnlikeInList(blogpostTitle: String) {
        webConfluence().dom.click(".post[alt='${blogpostTitle}'] .field-interaction .aui-iconfont-like")
    }

    fun awaitOneLike(blogpostTitle: String) {
        webConfluence().dom.awaitElementPresent(".post[alt='${blogpostTitle}'] .field-interaction [data-liked-by-user=\"true\"]")
    }

    fun awaitZeroLikes(blogpostTitle: String) {
        webConfluence().dom.awaitElementPresent(".post[alt='${blogpostTitle}'] .field-interaction [data-liked-by-user=\"false\"]")
    }

    fun startCreateBlogpostKeepOpen(blogpostTitle: String) {
        val webConfluence = webConfluence()
        val dom = webConfluence.dom
        webConfluence.navigateTo("pages/createblogpost.action?spaceKey=$spaceKey") //Create a new blog post
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#wysiwyg")
        val content = "<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
        webConfluence.setPageTitleInEditor(blogpostTitle)
        webConfluence.focusAndUnfocusEditor()
        dom.addTextTinyMce(content)
    }

    @After
    fun after() {
        webClient().quit()
    }

    private fun webConfluence() : WebConfluence {
        return this.webClient!! as WebConfluence
    }

}