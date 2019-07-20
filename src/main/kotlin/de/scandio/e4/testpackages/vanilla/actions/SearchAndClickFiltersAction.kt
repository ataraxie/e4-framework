package de.scandio.e4.testpackages.vanillaro.actions

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.clients.WebConfluence
import de.scandio.e4.testpackages.vanilla.actions.SearchAction
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class SearchAndClickFiltersAction (
    searchString: String
    ) : SearchAction(searchString) {

    override fun execute(webClient: WebClient, restClient: RestClient) {
        super.execute(webClient, restClient)
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.getDomHelper()
        this.start = Date().time
        clickAllLinksAndWaitForResults("date", ".cql-type-date .aui-nav a", dom, webConfluence)
        clickAllLinksAndWaitForResults("contentType", ".cql-type-contentType .aui-nav a", dom, webConfluence)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

    fun clickAllLinksAndWaitForResults(filterName: String, selector: String, dom: DomHelper, webConfluence: WebConfluence) {
        var index = 0
        val elements = dom.findElements(selector)
        for (element in elements) {
            index += 1
            dom.click(element)
            awaitNewSearchResultsVisible(dom)
            webConfluence.takeScreenshot("filter-$filterName-$index-click")
        }
    }

    fun awaitNewSearchResultsVisible(dom: DomHelper) {
        dom.awaitElementPresent(".search-results")
        dom.awaitElementInsivible(".search-blanket")
    }
}