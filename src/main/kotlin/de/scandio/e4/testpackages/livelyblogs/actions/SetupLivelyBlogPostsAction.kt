package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.rest.RestConfluence
import de.scandio.e4.worker.util.RandomData
import org.slf4j.LoggerFactory
import java.util.*

class SetupLivelyBlogPostsAction(
        val spaceKey: String,
        val howMany: Int
) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    private var numBlogpostsCreated = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val restConfluence = restClient as RestConfluence
        this.start = Date().time
        repeat(howMany) {
            val title = "Lively Blog Setup Blogpost ${Date().time}"
            val content = "<h1>This is a great blog post</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
            restConfluence.createBlogpost(spaceKey, title, content)
            numBlogpostsCreated += 1
            log.info("Created blogpost. Now created: {{}}", numBlogpostsCreated)
        }
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }
}