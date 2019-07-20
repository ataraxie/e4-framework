package de.scandio.e4.testpackages.pocketquery.pqjira

import de.scandio.e4.clients.WebJira
import de.scandio.e4.testpackages.pocketquery.pqjira.PocketQueryJiraSeleniumHelper
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class PocketQueryJiraSetupAction : Action() {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webJira = webClient as WebJira
        val pqHelper = PocketQueryJiraSeleniumHelper(webJira, webJira.dom)
        webJira.login()
        pqHelper.goToPocketQueryAdmin()
        this.start = Date().time
        pqHelper.createWikipediaSetup()
        pqHelper.openProjectConfig("TEST")
        pqHelper.setWikipediaQueryInCenter()
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }


}