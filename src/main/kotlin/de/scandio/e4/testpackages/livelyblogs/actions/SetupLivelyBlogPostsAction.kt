package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.worker.util.RandomData
import org.slf4j.LoggerFactory
import java.util.*

/**
 * === SetupLivelyBlogPostsAction ===
 *
 * Lively Blogs SetupLivelyBlogPostsAction action.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space $spaceKey exists
 * - Page $teaserImagePageTitle exists in $spaceKey
 *
 * Procedure (REST):
 * - Creates $howMany blog posts in space $spaceKey taking a random
 *   teaser image from $teaserImagePageTitle with a 33% change
 * - Add "important" labels with a chance of 20% for each post
 * - Add random labels "label{1,5}" with a chance of 50% for each post
 *
 * Result:
 * - $howMany blog posts were created
 * - 33% will have a teaser image
 * - 20% will have a "important" label
 * - 50% will have one or more labels "label{1,5}"
 *
 * @author Felix Grund
 */
class SetupLivelyBlogPostsAction(
        val spaceKey: String,
        val teaserImagePageTitle: String,
        val howMany: Int
) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    private val contentIds = arrayListOf<Long>()

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val restConfluence = restClient as RestConfluence
        this.start = Date().time
        repeat(howMany) { blogpostNumber ->
            var title = "Setup Blogpost #$blogpostNumber (${Date().time})"
            var content = "<h1>This is a great blog post</h1><p>${RandomData.STRING_LOREM_IPSUM}</p>"
            if (rnd("1", "2", "3") == "3") { // 1/3 of cases
                content += randomTeaserImageContent()
                title += " (teaser)"
            }

            val contentId = restConfluence.createBlogpost(spaceKey, title, content)
            contentIds.add(contentId)
        }

        for (contentId in contentIds) {
            val labels = arrayListOf<String>()
            if (rnd("1", "2", "3", "4", "5") == "5") { // 1/5 of cases
                labels.add("important")
            }
            if (rnd("1", "2") == "2") { // 1/3 of cases
                val howManyLabels = Random().nextInt(5) + 1
                repeat(howManyLabels) { labelNumber ->
                    labels.add("label${labelNumber+1}")
                }
            }

            if (labels.isNotEmpty()) {
                restConfluence.addLabelsToContentEntity(contentId, labels)
            }
        }

        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return end - start
    }

    fun randomTeaserImageContent(): String {
        return """
<p><ac:image ac:class="teaser" ac:thumbnail="true" ac:height="250"><ri:attachment ri:filename="random-image-${rnd("0,1,2,3,4,5,6,7,8,9".split(","))}.jpg"><ri:page ri:content-title="$teaserImagePageTitle" /></ri:attachment></ac:image></p>
        """.trimIndent().trimLines()
    }

    fun String.trimLines() = replace("\n", "")
}