package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.RandomData
import org.slf4j.LoggerFactory
import java.util.*

class SearchLoremIpsumAction : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        log.debug("SearchLoremIpsumAction.execute with web user {{}} and REST user {{}}", webClient.user, restClient.user)

        val webConfluence = webClient as WebConfluence
        webConfluence.login()
        val keywordSize = RandomData.LIST_LOREM_IPSUM.size
        val queryIndex = Random().nextInt(keywordSize)
        val query = RandomData.LIST_LOREM_IPSUM.get(queryIndex)
        this.start = Date().time
        webConfluence.search(query)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }



}