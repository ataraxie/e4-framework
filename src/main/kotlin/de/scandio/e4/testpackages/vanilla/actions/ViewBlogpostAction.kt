package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.WebClient
import java.net.URLEncoder
import java.util.*

class ViewBlogpostAction (

    val spaceKey: String,
    val blogpostTitle: String,
    val blogpostCreationDate: String
    ) : Action {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val confluence = webClient as WebConfluence
        confluence.login()
        this.start = Date().time
        confluence.goToBlogpost(spaceKey, blogpostTitle, blogpostCreationDate)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}