package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.RandomData
import java.util.*

class AddRandomCommentAction : Action() {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val restConfluence = restClient as RestConfluence
        // IMPORTANT: do this before measuring because it invokes a REST call!
        val randomContentId = restConfluence.getRandomContentId()
        webConfluence.login()
        webConfluence.goToPage(randomContentId)
        val randomHtml10Words = "<p>${RandomData.getRandomString(10)}</p>"
        this.start = Date().time
        webConfluence.addRandomComment(randomHtml10Words)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}