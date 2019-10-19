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
        var markdownFiles = webConfluence.getFilesFromInputDir("random-markdown.*\\.md")
        if (markdownFiles.isEmpty()) {
            repeat(10) {
                val filename = "random-markdown-$it.md"
                val path = "/markdown/$filename"
                val inputUrl = javaClass.getResource(path)
                val destUrl = "${webConfluence.inputDir}/$filename"
                FileUtils.copyURLToFile(inputUrl, File(destUrl))
            }
        }
        markdownFiles = webConfluence.getFilesFromInputDir("random-markdown.*\\.md")
        val randInt = WorkerUtils.getRandomItem(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
        val markdownFile = markdownFiles[randInt]
        val markdownFileString = FileUtils.readFileToString(markdownFile, StandardCharsets.UTF_8);
        val randomContentId = restConfluence.getRandomContentId(spaceKey, parentPageTitle)
        val dom = webConfluence.dom
        webConfluence.login()
        this.start = Date().time
        webConfluence.goToPage(randomContentId, ".quick-editor-prompt")
        var diaryEntriesOnPage = 0
        try {
            diaryEntriesOnPage = dom.findElements("li.entry:not(.welcome-message)").size
            log.info("$diaryEntriesOnPage elements on page so far")
        } catch (e: Exception) {
            log.info("No elements on page yet")
        }
        dom.click(".quick-editor-prompt")
        dom.awaitElementPresent("#wysiwygTextarea_ifr")
        diaryEntriesOnPage += 1
        webConfluence.insertMarkdown(markdownFileString)
        dom.removeElementWithJQuery(".aui-blanket")
        dom.awaitMilliseconds(50)
        dom.click("#rte-button-publish")
        dom.awaitElementClickable("li.entry:not(.welcome-message):nth-child(${diaryEntriesOnPage})")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}