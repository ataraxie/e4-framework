package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.WebConfluence
import de.scandio.e4.worker.rest.RestConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class CreatePageAction(
        val spaceKey: String,
        var pageTitle: String,
        var pageContent: String = "",
        var useRest: Boolean = false,
        var appendUsernameToPageTitle: Boolean = false,
        var appendTimestampToPageTitle: Boolean = false
) : Action() {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val username = restClient.user
        if (appendUsernameToPageTitle) {
            pageTitle += " ($username)"
        }

        if (appendTimestampToPageTitle) {
            pageTitle += " (${Date().time})"
        }


        if (useRest) {
            val restConfluence = restClient as RestConfluence
            this.start = Date().time
            restConfluence.createPage(spaceKey, pageTitle, pageContent)
        } else {
            val webConfluence = webClient as WebConfluence
            webConfluence.login()
            this.start = Date().time
            if (pageContent.isEmpty()) {
                webConfluence.createDefaultPage(spaceKey, pageTitle)
            } else {
                webConfluence.createCustomPage(spaceKey, pageTitle, pageContent)
            }
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