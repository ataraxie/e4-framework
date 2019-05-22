package de.scandio.e4.testpackages.vanilla.scenarios

import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class QuicksearchScenario (
    val searchString: String
    ) : Scenario {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        var restUrl = "rest/quicknav/1/search?query=$searchString"
        val restConfluence = restClient as RestConfluence
        this.start = Date().time
        val responseStatus = restConfluence.sendGetRequestReturnStatus(restUrl)
        if (responseStatus != 200) {
            throw Exception("Response status for quicksearch REST call was not 200 but $responseStatus")
        }
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }
}