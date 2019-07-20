package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.WebConfluence
import de.scandio.e4.worker.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class CreateSpaceAction(
        val spaceKey: String,
        val spaceName: String,
        val useRest: Boolean = false
) : Action() {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        if (useRest) {
            val restConfluence = restClient as RestConfluence
            this.start = Date().time
            restConfluence.createSpace(spaceKey, spaceName)
        } else {
            val webConfluence = webClient as WebConfluence
            webConfluence.login()
            this.start = Date().time
            webConfluence.createEmptySpace(spaceKey, spaceName)
        }
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

    override fun isRestOnly(): Boolean {
        return useRest
    }

}