package de.scandio.e4.testpackages.gitsnippets.actions

import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.WorkerUtils
import org.slf4j.LoggerFactory
import java.util.*

open class SetupDiaryMacroPagesAction (
        val spaceKey: String,
        val parentPageTitle: String,
        val howMany: Int = 1
    ) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val restConfluence = restClient as RestConfluence
        val macroId = "diary"
        this.start = Date().time
        try {
            repeat(howMany) {
                val pageTitle = "Setup Diary Page $macroId #$it (${Date().time})"
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
            <ac:structured-macro ac:name="diary">
                <ac:parameter ac:name="order">${rnd("ascending", "descending")}</ac:parameter>
                <ac:parameter ac:name="style">${rnd("simple", "bubble")}</ac:parameter>
                <ac:parameter ac:name="editorposition">${rnd("top", "bottom")}</ac:parameter>
                <ac:parameter ac:name="showtitle">${rnd("false", "true")}</ac:parameter>
                <ac:parameter ac:name="time">${rnd("false", "true")}</ac:parameter>
                <ac:rich-text-body><p><br /></p></ac:rich-text-body>
            </ac:structured-macro>
        """.trimIndent()
    }

}