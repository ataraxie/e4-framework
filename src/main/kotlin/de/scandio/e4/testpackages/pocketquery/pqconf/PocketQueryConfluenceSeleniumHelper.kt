package de.scandio.e4.testpackages.pocketquery.pqconf

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.testpackages.pocketquery.PocketQuerySeleniumHelper
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class PocketQueryConfluenceSeleniumHelper(
        webClient: WebClient,
        dom: DomHelper
) : PocketQuerySeleniumHelper(webClient, dom) {

    fun insertPocketQueryMacro(queryName: String, paramsToCheck: List<String> = arrayListOf()) {
        val webConfluence = webClient as WebConfluence
        webConfluence.openMacroBrowser("pocketquery", "pocketquery")
        setQueryInMacroBrowser(queryName)
        for (macroParam in paramsToCheck) {
            dom.click("#macro-param-$macroParam")
        }
        webConfluence.saveMacroBrowser()
    }

    fun setQueryInMacroBrowser(queryName: String) {
        setSelect2Option("#query-name-select", queryName)
    }

    fun createPocketQueryPage(queryName: String, selectorToBePresent: String, macroParamsToCheck: List<String> = arrayListOf()) {
        val pageTitle = "PQ TestBasicSqlSetup (${Date().time})"
        webConfluence().createDefaultPage("PQ", pageTitle)
        webConfluence().goToEditPage()
        insertPocketQueryMacro(queryName, macroParamsToCheck)
        webConfluence().savePage()
        dom.awaitElementPresent(selectorToBePresent, 40)
    }

    private fun webConfluence() : WebConfluence {
        return this.webClient!! as WebConfluence
    }

}