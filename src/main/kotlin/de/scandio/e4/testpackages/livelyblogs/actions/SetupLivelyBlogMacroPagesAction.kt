package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*


open class SetupLivelyBlogMacroPagesAction (
        val spaceKey: String,
        val parentPageTitle: String = "",
        val howMany: Int = 1
    ) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val restConfluence = restClient as RestConfluence
        val macroId = "lively-blog-posts"
        this.start = Date().time
        try {
            repeat(howMany) {
                val pageTitle = "Setup Macro Page $macroId #$it (${Date().time})"
                val macroPageContent = createRandomStorageFormat()
                restConfluence.createPage(spaceKey, pageTitle, macroPageContent, parentPageTitle)
            }
        } catch (e: Exception) {
            log.warn("Failed creating page. Skipping.")
        }

        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

    override fun isRestOnly(): Boolean {
        return true
    }

    fun createRandomStorageFormat() : String {
        return  """
            <ac:structured-macro ac:name="lively-blog-posts">
                <ac:parameter ac:name="layout">${rnd("default","image-left","image-right","image-embedded","image-fullwidth")}</ac:parameter>
                <ac:parameter ac:name="priorityTimeFrame">${rnd("3d", "5d", "10d")}</ac:parameter>
                <ac:parameter ac:name="priorityMax">${rnd("5", "10", "20")}</ac:parameter>
                <ac:parameter ac:name="max">${rnd("5", "10", "30")}</ac:parameter>
                <ac:parameter ac:name="renderTextFormatting">${rnd("true", "false")}</ac:parameter>
                <ac:parameter ac:name="style">${rnd("confluence", "lively")}</ac:parameter>
                <ac:parameter ac:name="sort">${rnd("modified", "created", "latest comment")}</ac:parameter>
                <ac:parameter ac:name="renderNewlines">${rnd("true", "false")}</ac:parameter>
                <ac:parameter ac:name="timeFrame">${rnd("5d", "10d", "20d")}</ac:parameter>
                <ac:parameter ac:name="labels">${randomLabelSometimes()}</ac:parameter>
            </ac:structured-macro>
        """.trimIndent()
    }

    fun randomLabelSometimes(): String {
        var label = ""
        if (rnd("1", "2", "3") == "3") {
            label = "label${Random().nextInt(5) + 1}"
        }
        return label
    }

}