package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.RandomData
import org.openqa.selenium.Keys
import org.openqa.selenium.interactions.Actions
import org.slf4j.LoggerFactory
import java.util.*

/**
 * === CreateRandomLivelyBlogPost ===
 *
 * Lively Blogs CreateRandomLivelyBlogPost action.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space with $spaceKey exists
 *
 * Procedure (SELENIUM):
 * - Create a blogpost in space $spaceKey with a 1/3 chance having
 *   an image from $attachmentPageTitle as teaser image
 *
 * Result:
 * - Blog post was created
 *
 * @author Felix Grund
 */
open class CreateRandomLivelyBlogPost (
        val spaceKey: String,
        val attachmentPageTitle: String
) : Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        webConfluence.login()
        this.start = Date().time
        webConfluence.navigateTo("pages/createblogpost.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#wysiwyg")
        val content = "<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
        webConfluence.setPageTitleInEditor("Lively Blog Post (${Date().time})")
        webConfluence.focusEditor()
        dom.addTextTinyMce(content)
        if (rnd("1", "2", "3") == "3") { // create teaser image for 1/3 of posts
            webConfluence.insertRandomImageFromPage(attachmentPageTitle)
        }
        webConfluence.savePage()

        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}