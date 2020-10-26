package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.E4Env
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.util.*

class LivelyBlog_3_10_0 : AbstractLivelyBlogTestSuite() {

    companion object {
        // NOTE: If you re-use the space (i.e. no PREPARATION_RUN flag set, you'll have to make sure your space is set as
        // the featured space in the global LB settings.
        val spaceKey = if (E4Env.PREPARATION_RUN) "LB${Date().time}" else "LB"

        @BeforeClass
        @JvmStatic internal fun beforeAll() {
            if (E4Env.PREPARATION_RUN) {
                runWithDump {
                    restConfluence.createSpace(spaceKey, spaceKey)
                    webConfluence.login()
                    helper.setupFeaturedSpace(spaceKey)
                    // First, upload some attachments to some new random page such that we can create teaser images
                    val pageId = restConfluence.createPage(spaceKey, "LB E4 Attachment Page")
                    val images = webConfluence.prepareImages("random-image-1.jpg")
                    webConfluence.uploadImages(pageId, images)
                }
            }
        }
        @AfterClass
        @JvmStatic internal fun afterAll() {
            webClient.quit()
        }
    }

    // LBCSRV-16: tabs on Overview Preview
    @Test
    fun LBCSRV_16() {
        runWithDump {
            val categoryName = "E4${Date().time}"
            val blogpostTitle = "E4 LB Blog Post with Label '${categoryName}'"
            val blogpostId = restConfluence.createBlogpost(spaceKey, blogpostTitle, "Random Content")
            restConfluence.addLabelsToContentEntity(blogpostId, arrayListOf(categoryName))
            webConfluence.login()
            val categoryId = helper.addLivelyBlogCategoryReturnId(categoryName)
            webConfluence.createPageKeepOpen(spaceKey, "E4 LB Page")
            webConfluence.openMacroBrowser("lively-blog-posts-overview", "lively-blog-posts-overview")
            dom.awaitSeconds(3)
            webConfluence.focusMacroBrowserPreviewFrame()
            dom.click(".lively-blog-categories li[data-filter='category:${categoryId}'] a")
            awaitBlogpostPresentInList(blogpostTitle)
            dom.awaitSeconds(1)
        }
    }

    // LBCSRV-36: comments before likes
    @Test
    fun LBCSRV_36() {
        runWithDump {
            webConfluence.login()
            val blogpostTitle = webConfluence.createBlogpostAndSave(spaceKey, "LB E4 Blog Post")
            webConfluence.addRandomComment("Heeellooo this is a Comment oh yeah!!")
            webConfluence.likeOrUnlikePageOrBlogpost()
            dom.awaitSeconds(3) // give index a bit
            webConfluence.goToDashboard()
            dom.awaitSeconds(3)
            dom.awaitElementPresent(".post[data-title='${blogpostTitle}'] .field-interaction")
            // Likes must be in the first immediate child of the .field-interaction container...
            dom.expectElementPresent(".post[data-title='${blogpostTitle}'] .field-interaction > *:first-child .likes")
            // ...and comments must be in the one after!
            dom.expectElementPresent(".post[data-title='${blogpostTitle}'] .field-interaction > *:last-child .aui-iconfont-comment")
        }
    }

    // LBCSRV-32: teaser warning
    @Test
    fun LBCSRV_32() {
        runWithDump {
            webConfluence.login()
            webConfluence.createBlogpostKeepOpen(spaceKey, "E4 LB Blog Post")
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
            webConfluence.savePageOrBlogPost()
        }
    }

    // LBCSRV-31: direct likes
    @Test
    fun LBCSRV_31() {
        runWithDump {
            webConfluence.login()
            val blogpostTitle = webConfluence.createBlogpostKeepOpen(spaceKey, "LB E4 Blog Post") // create a blog post
            webConfluence.savePageOrBlogPost() // save it
            dom.awaitSeconds(3) // give index a bit
            helper.goToBlogOverview() // go to blog overview
            awaitBlogpostPresentInList(blogpostTitle)
            expectNoLikesButton(blogpostTitle) // there should be no likes button for the blog post
            webConfluence.goToDashboard() // go to the dashboard
            awaitBlogpostPresentInList(blogpostTitle)
            expectNoLikesButton(blogpostTitle) // there should be no likes button for the blog post
            dom.click(".post[data-title=\"${blogpostTitle}\"] .title a") // go to blogpost
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
            webConfluence.login()
            val blogpostTitle = webConfluence.createBlogpostKeepOpen(spaceKey, "E4 LB Blog Post")
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
            dom.awaitSeconds(4) // Give the index a few seconds
            webConfluence.goToDashboard()
            dom.awaitSeconds(1)
            expectTeaserIsDisplayed(blogpostTitle)
            dom.click(".post[data-title=\"${blogpostTitle}\"] img[alt=\"${blogpostTitle}\"]")
            dom.awaitElementPresent("#main-content")
            webConfluence.goToEditCurrentPage()
            clickImage()
            expectButtonActive()
            clickTeaserButton()
            expectButtonNotActive()
            webConfluence.savePageOrBlogPost()
            dom.awaitSeconds(3) // Again, give it a few seconds
            webConfluence.goToDashboard()
            dom.awaitSeconds(1)
            expectTeaserIsNotDisplayed(blogpostTitle)
        }
    }

    fun expectNoLikesButton(blogpostTitle: String) {
        dom.expectElementNotPresent(".post[data-title=\"${blogpostTitle}\"] .field-interaction .likes")
    }

    fun expectLikesButton(blogpostTitle: String) {
        dom.expectElementPresent(".post[data-title=\"${blogpostTitle}\"] .field-interaction .likes")
    }

    fun expectTeaserIsDisplayed(blogpostTitle: String) {
        awaitBlogpostPresentInList(blogpostTitle)
        dom.expectElementPresent(".post[data-title=\"${blogpostTitle}\"] img[alt=\"${blogpostTitle}\"]")
    }

    fun expectTeaserIsNotDisplayed(blogpostTitle: String) {
        awaitBlogpostPresentInList(blogpostTitle)
        dom.expectElementNotPresent(".post[data-title=\"${blogpostTitle}\"] img[alt=\"${blogpostTitle}\"]")
    }

    fun awaitBlogpostPresentInList(blogpostTitle: String) {
        dom.awaitElementPresent(".post[data-title=\"${blogpostTitle}\"]")
    }

    fun expectButtonActive() {
        webConfluence.unfocusEditor()
        dom.expectElementPresent(".lively-blog-set-teaser.active")
    }

    fun expectButtonNotActive() {
        webConfluence.unfocusEditor()
        dom.expectElementNotPresent(".lively-blog-set-teaser.active")
    }

    fun clickTeaserButton() {
        webConfluence.unfocusEditor()
        dom.click(".lively-blog-set-teaser")
        dom.awaitSeconds(1)
    }

    fun clickProperties() {
        webConfluence.unfocusEditor()
        dom.click(".aui-button.image-properties")
        dom.awaitSeconds(1)
    }

    fun unfocusImage() {
        webConfluence.unfocusEditor()
        dom.click("#content-title-div")
        dom.awaitSeconds(1)
    }

    fun clickImage() {
        webConfluence.focusEditor()
        dom.click(".confluence-embedded-image")
        dom.awaitSeconds(1)
    }

    fun likeOrUnlikeInList(blogpostTitle: String) {
        dom.click(".post[data-title=\"${blogpostTitle}\"] .field-interaction .aui-iconfont-like")
    }

    fun awaitOneLike(blogpostTitle: String) {
        dom.awaitElementPresent(".post[data-title=\"${blogpostTitle}\"] .field-interaction [data-liked-by-user=\"true\"]")
    }

    fun awaitZeroLikes(blogpostTitle: String) {
        dom.awaitElementPresent(".post[data-title=\"${blogpostTitle}\"] .field-interaction [data-liked-by-user=\"false\"]")
    }

}