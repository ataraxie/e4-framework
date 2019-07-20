package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.WebConfluence
import de.scandio.e4.worker.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*


open class ViewRandomContent(
        val spaceKey: String? = null,
        val parentPageTitle: String? = null
) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        log.debug("ViewRandomContent.execute with web user {{}} and REST user {{}}", webClient.user, restClient.user)
        val webConfluence = webClient as WebConfluence
        val restConfluence = restClient as RestConfluence
        // IMPORTANT: do this before measuring because it invokes a REST call!
        val randomContentId = restConfluence.getRandomContentId(spaceKey, parentPageTitle)
        webConfluence.login()
        this.start = Date().time
        webConfluence.goToPage(randomContentId)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}