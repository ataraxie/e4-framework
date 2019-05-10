package de.scandio.e4.testpackages.delaymacro.scenarios

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*

class ViewDelayPageScenario: Scenario {

    private val log = LoggerFactory.getLogger(javaClass)
    private var startTime: Long = 0
    private var endTime: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        log.info("Executing ViewDelayPageScenario")
        webClient as WebConfluence
        this.startTime = Date().time
        webClient.goToLogin()
        webClient.takeScreenshot("login")
        this.endTime = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.endTime - this.startTime
    }


}