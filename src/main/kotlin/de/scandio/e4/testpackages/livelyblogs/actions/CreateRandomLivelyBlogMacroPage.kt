package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

/**
 * === CreateRandomLivelyBlogMacroPage ===
 *
 * Lively Blogs CreateRandomLivelyBlogMacroPage action.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space with $spaceKey exists
 *
 * Procedure (SELENIUM):
 * - Create a page in space $spaceKey containing the Lively Blog Posts macro with
 *   random macro parameters in the page content
 *
 * Result:
 * - Page with Lively Blog Posts macro was created
 *
 * @author Felix Grund
 */
open class CreateRandomLivelyBlogMacroPage (
        val spaceKey: String
) : Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        val macroId = "lively-blog-posts"
        val pageTitle = "Macro Page $macroId (${Date().time})"
        webConfluence.login()
        this.start = Date().time
        webConfluence.navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#wysiwyg")
        webConfluence.setPageTitleInEditor(pageTitle)
        val paramMap = mapOf(
                "layout" to rnd("default","image-left","image-right","image-embedded","image-fullwidth"),
                "priorityTimeFrame" to rnd("3d", "5d", "10d"),
                "priorityMax" to rnd("5", "10", "20"),
                "max" to rnd("5", "10", "30"),
                "renderTextFormatting" to rnd("true", "false"),
                "style" to rnd("confluence", "lively"),
                "sort" to rnd("modified", "created", "latest comment"),
                "renderNewlines" to rnd("true", "false"),
                "timeFrame" to rnd("5d", "10d", "20d"),
                "labels" to randomLabelSometimes()
        )
        webConfluence.insertMacro(macroId, macroId, paramMap)
        // dom.addTextTinyMce(paramMap.toString()) // outcomment to verify macro parameters in page content
        webConfluence.savePageOrBlogPost()
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

    fun randomLabelSometimes(): String {
        var label = ""
        if (rnd("1", "2", "3") == "3") {
            label = "label${Random().nextInt(5) + 1}"
        }
        return label
    }


}