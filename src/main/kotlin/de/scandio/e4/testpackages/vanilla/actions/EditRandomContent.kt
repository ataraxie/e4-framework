package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.RandomData
import org.slf4j.LoggerFactory
import java.util.*


open class EditRandomContent : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        log.debug("EditRandomContent.execute with web user {{}} and REST user {{}}", webClient.user, restClient.user)
        val webConfluence = webClient as WebConfluence
        val restConfluence = restClient as RestConfluence
        // IMPORTANT: do this before measuring because it invokes a REST call!
        val randomContentId = restConfluence.randomContentId
        webConfluence.login()
        val randomHtml10Words = "<p>${RandomData.getRandomString(10)}</p>"
        this.start = Date().time
        webConfluence.editPageAddContent(randomContentId, randomHtml10Words)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}