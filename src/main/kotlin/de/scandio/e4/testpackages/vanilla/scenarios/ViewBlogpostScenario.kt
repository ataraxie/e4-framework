package de.scandio.e4.testpackages.vanilla.scenarios

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.WebClient
import java.net.URLEncoder
import java.util.*

class ViewBlogpostScenario (

    val spaceKey: String,
    val blogpostTitle: String,
    val blogpostCreationDate: String
    ) : Scenario {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val confluence = webClient as WebConfluence
        val dom = DomHelper(confluence)
        val encodedPageTitle = URLEncoder.encode(this.blogpostTitle, "utf-8")
        confluence.login()
        confluence.takeScreenshot("after-login")

        this.start = Date().time
        confluence.goToBlogpost("E4", this.blogpostTitle, this.blogpostCreationDate)
        dom.awaitElementPresent("#main-content")
        confluence.takeScreenshot("view-page-$spaceKey-$encodedPageTitle")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}