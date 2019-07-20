package de.scandio.e4.testpackages.livelytheme.actions

import de.scandio.e4.worker.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*

/**
 * === SetupLivelyThemeMacroPages ===
 *
 * Lively Theme SetupLivelyThemeMacroPages action.
 *
 * Assumptions:
 * - Lively Theme app installed
 *
 * Procedure (SELENIUM):
 * - Creates $howMany pages with random Lively Theme macros as children of page $parentPageTitle in
 *   space with key $spaceKey
 *
 * Result:
 * - Page with title $parentPageTitle in space $spaceKey has $howMany child pages with random LT macros
 *
 * @author Felix Grund
 */
open class SetupLivelyThemeMacroPages (
        val spaceKey: String,
        val parentPageTitle: String = "",
        val howMany: Int = 1,
        val macroPageContentMap: Map<String, String>
    ) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val restConfluence = restClient as RestConfluence
        val numMacros = macroPageContentMap.size
        this.start = Date().time
        try {
            repeat(howMany) {
                val randIndex = Random().nextInt(numMacros)
                val macroId = macroPageContentMap.keys.toTypedArray()[randIndex]
                val pageTitle = "Macro Page $macroId (${Date().time})"
                val macroPageContent = macroPageContentMap[macroId]
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

}