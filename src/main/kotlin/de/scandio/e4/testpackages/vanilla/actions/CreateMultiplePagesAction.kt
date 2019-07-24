package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.RandomData
import java.util.*

class CreateMultiplePagesActionRest(
        val spaceKey: String,
        val parentPageTitle: String,
        val howMany: Int
) : Action() {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        repeat(howMany) {
            val pageTitle = "CreateMultiplePagesAction (${Date().time})"
            val pageContent = "<h1>Lorem Ipsum</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
            val restConfluence = restClient as RestConfluence
            this.start = Date().time
            restConfluence.createPage(spaceKey, pageTitle, pageContent, parentPageTitle)
            this.end = Date().time
            Thread.sleep(10)
        }

    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

    override fun isRestOnly(): Boolean {
        return true
    }
}