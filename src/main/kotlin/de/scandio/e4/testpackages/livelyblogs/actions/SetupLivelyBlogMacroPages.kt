package de.scandio.e4.testpackages.livelyblogs.actions

import de.scandio.e4.worker.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*


open class SetupLivelyBlogMacroPages (
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
        val pageTitle = "Macro Page $macroId (${Date().time})"
        this.start = Date().time
//        try {
//            repeat(howMany) {
//                restConfluence.createPage(spaceKey, pageTitle, macroPageContent, parentPageTitle)
//            }
//        } catch (e: Exception) {
//            log.warn("Failed creating page. Skipping.")
//        }

        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

    override fun isRestOnly(): Boolean {
        return true
    }

    fun createRandomStorageFormat() {
        val template =  """
            <ac:structured-macro ac:name="lively-blog-posts">
                <ac:parameter ac:name="layout">image-left</ac:parameter>
                <ac:parameter ac:name="priorityTimeFrame">10d</ac:parameter>
                <ac:parameter ac:name="priorityMax">5</ac:parameter>
                <ac:parameter ac:name="max">20</ac:parameter>
                <ac:parameter ac:name="renderTextFormatting">true</ac:parameter>
                <ac:parameter ac:name="style">confluence</ac:parameter>
                <ac:parameter ac:name="sort">modified</ac:parameter>
                <ac:parameter ac:name="renderNewlines">true</ac:parameter>
                <ac:parameter ac:name="timeFrame">20d</ac:parameter>
                <ac:parameter ac:name="labels">test</ac:parameter>
            </ac:structured-macro>
        """.trimIndent()
    }

}