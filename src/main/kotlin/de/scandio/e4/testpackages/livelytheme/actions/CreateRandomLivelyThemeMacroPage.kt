package de.scandio.e4.testpackages.livelytheme.actions

import de.scandio.e4.clients.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*

/**
 * === CreateRandomLivelyThemeMacroPage ===
 *
 * Lively Theme CreateRandomLivelyThemeMacroPage action.
 *
 * Assumptions:
 * - Lively Theme app installec
 * - Space with key $spaceKey
 *
 * Procedure (SELENIUM):
 * - Choose a Lively Theme macro by random
 * - Create page in space $spaceKey with title containing macro ID and creation date
 * - Insert Lively Theme macro chosen selected by random in step 1
 * - Add page content that makes sense in combination with macro
 *
 * Result:
 * - Page created in space $spaceKey with random LT macro in its content
 *
 * @author Felix Grund
 */
open class CreateRandomLivelyThemeMacroPage (
        val spaceKey: String
) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    var webConfluence: WebConfluence? = null

    val MACRO_IDS_AND_NAMES = mapOf(
            "lively-button" to "Button",
            "lively-column-width" to "Column Width",
            "lively-widget" to "Widget",
            "lively-menu" to "Menu",
            "lively-list" to "List",
            "lively-margin" to "Margin"
    )

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        this.webConfluence = webConfluence
        val dom = webConfluence.dom
        val numMacros = MACRO_IDS_AND_NAMES.keys.size
        val randIndex = Random().nextInt(numMacros)
        val macroId = MACRO_IDS_AND_NAMES.keys.toTypedArray()[randIndex]
        val pageTitle = "Macro Page $macroId (${Date().time})"
        webConfluence.login()
        this.start = Date().time
        webConfluence.navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#wysiwyg")
        webConfluence.setPageTitleInEditor(pageTitle)
        when(macroId) {
            "lively-button" -> {
                insertLivelyMacro(macroId, mapOf(
                        "text" to "My Button",
                        "link" to "Lively Theme Home"
                ))
            }
            "lively-list" -> {
                insertLivelyMacro(macroId)
            }
            "lively-menu" -> {
                insertLivelyMacro(macroId)
                dom.click("#rte-button-bullist")
                webConfluence.simulateBulletList(arrayOf("Test 1", "Test 2", "Test 3"))
            }
            "lively-widget" -> {
                insertLivelyMacro(macroId)
                webConfluence.simulateText("Test")
            }
            "lively-margin" -> {
                insertLivelyMacro(macroId, mapOf(
                        "margin" to "300px"
                ))
                webConfluence.simulateText("Test")
            }
            "lively-column-width" -> {
                webConfluence.focusEditor()
                webConfluence.setTwoColumnLayout()
                webConfluence.simulateText("COLUMN")
                insertLivelyMacro(macroId, mapOf("width" to "300px"))
            }
        }
        webConfluence.savePage()
        this.end = Date().time
    }

    fun insertLivelyMacro(macroId: String, macroParameters: Map<String, String> = emptyMap()) {
        webConfluence!!.insertMacro(macroId, MACRO_IDS_AND_NAMES[macroId]!!, macroParameters)
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}