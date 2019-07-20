package de.scandio.e4.testpackages.pocketquery.pqjira

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.testpackages.pocketquery.PocketQuerySeleniumHelper
import de.scandio.e4.worker.interfaces.WebClient

class PocketQueryJiraSeleniumHelper(
        webClient: WebClient,
        dom: DomHelper
) : PocketQuerySeleniumHelper(webClient, dom) {

    fun openProjectConfig(projectKey: String) {
        webClient.navigateTo("plugins/servlet/pocketquery/projectconfig?projectKey=$projectKey")
        dom.awaitElementPresent("#pocketquery-project-settings-container")
        webClient.takeScreenshot("DONE")
    }

    fun setWikipediaQueryInCenter() {
        TODO("IMPLEMENT")
        //val select = dom.findElement("#select-query")

        //setSelect2Option("#select-query", "")
    }


}