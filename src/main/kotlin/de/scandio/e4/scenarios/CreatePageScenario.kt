package de.scandio.e4.scenarios

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory

class CreatePageScenario (
        private val pageTitle: String,
        private val spaceKey: String,
        private val pageContent: String,
        private val parentPageId: String
): Scenario {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val restConfluence = restClient as RestConfluence
        log.debug("CreatePageScenario.execute with web driver {{}}", webConfluence.driver)

        // TODO not working yet
        //restConfluence.createPage(pageTitle, spaceKey, pageContent, parentPageId)
    }

}