package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

open class SearchAction (
    val searchString: String
    ) : Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.getDomHelper()
        webConfluence.login()
        this.start = Date().time

        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }
}