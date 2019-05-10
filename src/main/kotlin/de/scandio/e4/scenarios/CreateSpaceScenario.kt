package de.scandio.e4.scenarios

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory

class CreateSpaceScenario(
        private val spaceKey: String,
        private val spaceName: String
): Scenario {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val restConfluence = restClient as RestConfluence
        log.debug("CreateSpaceScenario.execute with web driver {{}}", webConfluence.driver)

        // TODO rest call not working yet
        //restConfluence.createSpace(spaceKey, spaceName)
    }

    override fun getTimeTaken(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}