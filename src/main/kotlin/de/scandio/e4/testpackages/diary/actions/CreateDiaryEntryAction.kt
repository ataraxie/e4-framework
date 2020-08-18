package de.scandio.e4.testpackages.diary.actions

import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.WorkerUtils
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import java.nio.file.Paths
import java.nio.file.Files.readAllBytes
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files


class CreateDiaryEntryAction (
        val spaceKey: String,
        val parentPageTitle: String
) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val restConfluence = restClient as RestConfluence
        val randomContentId = restConfluence.getRandomContentId(spaceKey, parentPageTitle)
        val dom = webConfluence.dom
        webConfluence.login()
        this.start = Date().time
        webConfluence.goToPage(randomContentId, ".quick-editor-prompt")
        dom.click(".quick-editor-prompt")
        dom.awaitElementPresent("#wysiwygTextarea_ifr")
        webConfluence.insertMarkdown(webConfluence.getRandomMarkdownContent())
        dom.removeElementWithJQuery(".aui-blanket")
        dom.awaitMilliseconds(200)
//        dom.executeScript("$('#rte-button-publish').click()")
        dom.click("#rte-button-publish")
        dom.awaitElementClickable("li.entry.focused")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}