package de.scandio.e4.uitest.pqconf

import org.junit.Test

/**
 * Assumptions:
 * - PQ is installed and licensed
 */
class PocketQuery_3_12_0 : AbstractPocketQueryConfluenceTestSuite() {

    @Test // XML responses (https://help.scandio.de/documentation/x/AoBbCQ)
    fun PQCSRV_8() {
        try {
            webConfluence.login()
            helper.goToPocketQueryAdmin()
            val datasourceName = helper.createRestCustomDatasource("GithubRawDataSource", "https://raw.githubusercontent.com/", "Used for testing with raw Github content")
            val queryName = helper.createRestQuery(datasourceName, "GithubRawXmlQuery", "/fundapps/api-examples/master/Sample-XML/Swap.xml")
            val converterName = helper.createConverter("GithubRawXmlConverter", """
                function convert(json) {
                    var parsedJsonObject = JSON.parse(json);
                    return [{Snapshot: JSON.stringify(parsedJsonObject.Snapshot)}];
                }
            """.trimIndent().trimLines(), "Used for testing with raw Github content")
            helper.setConverterOnQuery(queryName, converterName)

            helper.createPocketQueryPage(SPACEKEY, queryName)
            dom.awaitElementPresent(DEFAULT_RESULT_SELECTOR, 10)
            dom.awaitHasText(".pocketquery-result table tbody tr:first-child td", "Instruments")
        } finally {
            dump()
            shot()
        }
    }

}