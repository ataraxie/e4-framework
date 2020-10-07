package de.scandio.e4.testpackages.gitsnippets.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.gitsnippets.GitSnippetsTestPackage
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.WorkerUtils
import java.util.*

open class CreateRandomGitSnippetsMacroPage (
        val spaceKey: String
) : Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        val macroId = "live-snippet"
        val pageTitle = "Macro Page $macroId (${Date().time})"
        webConfluence.login()
        this.start = Date().time
        webConfluence.navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#wysiwyg")
        webConfluence.setTitleInEditor(pageTitle)
        val paramMap = mapOf(
                "bitbucketUrl" to "${GitSnippetsTestPackage.REPOSITORY_PATH}/${WorkerUtils.getRandomItem(GitSnippetsTestPackage.FILE_PATHS)}"
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