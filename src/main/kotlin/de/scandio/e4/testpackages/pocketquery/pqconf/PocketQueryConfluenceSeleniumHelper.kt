package de.scandio.e4.testpackages.pocketquery.pqconf

import de.scandio.e4.testpackages.pocketquery.PocketQuerySeleniumHelper
import de.scandio.e4.worker.interfaces.WebClient

class PocketQueryConfluenceSeleniumHelper(
        webClient: WebClient
) : PocketQuerySeleniumHelper(webClient) {

    fun insertPocketQueryMacroKeepOpen(
            queryName: String, paramsToCheck: List<String> = arrayListOf(),
            queryParameters: Map<String, String> = mapOf()) {

        webConfluence.openMacroBrowser("pocketquery", "pocketquery")
        dom.awaitElementPresent("#pq-preview-form aui-toggle")
        setQueryInMacroBrowser(queryName)
        for (paramKey in paramsToCheck) {
            dom.click("#pq-field-$paramKey")
            dom.awaitMilliseconds(100)
        }
        for (paramKey in queryParameters.keys) {
            val paramValue = queryParameters[paramKey]
            dom.insertText("input[data-parametername=\"$paramKey\"", paramValue!!)
        }
    }

    fun insertPocketQueryMacro(
            queryName: String, paramsToCheck: List<String> = arrayListOf(),
            queryParameters: Map<String, String> = mapOf()) {

        insertPocketQueryMacroKeepOpen(queryName, paramsToCheck, queryParameters)
        savePreview()
    }

    fun savePreview() {
        dom.click("#pq-dialog-preview button.confirm")
    }

    fun setQueryInMacroBrowser(queryName: String) {
        dom.setSelect2OptionByText("select#pq-field-queryName", queryName)
    }

    fun createPocketQueryPage(
            spaceKey: String,
            queryName: String,
            macroParamsToCheck: List<String> = arrayListOf(),
            queryParameters: Map<String, String> = mapOf()) {

        webConfluence.createPageKeepOpen(spaceKey, "PQ E4")
        insertPocketQueryMacro(queryName, macroParamsToCheck, queryParameters)
        webConfluence.savePageOrBlogPost()
    }

}