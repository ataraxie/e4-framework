package de.scandio.e4.uitest.pqconf

import de.scandio.e4.testpackages.pocketquery.PocketQuerySeleniumHelper.Companion.WIKIPEDIA_QUERY_JSON_PATH
import de.scandio.e4.testpackages.pocketquery.PocketQuerySeleniumHelper.Companion.WIKIPEDIA_QUERY_URL
import org.junit.Test

/**
 * Assumptions:
 * - PQ is installed and licensed
 */
class PocketQuery_3_9_3 : AbstractPocketQueryConfluenceTestSuite() {

    @Test // PRODUCTS-1419: NullPointerException for $PocketQuery.renderPocketQueryMacro
    fun testHelperRenderPocketQueryMacro() {
        runWithDump {
            webConfluence.login()
            helper.goToPocketQueryAdmin()
            val querySetup = helper.createWikipediaSetup()
            val outerTemplate = "\$PocketQuery.renderPocketQueryMacro('${querySetup.queryName}')"

            helper.setTemplateOnQuery(querySetup.queryName, querySetup.templateName)
            helper.setConverterOnQuery(querySetup.queryName, querySetup.converterName)

            val outerTemplateName = helper.createTemplate("RenderConfluenceMacro", outerTemplate)
            val outerQueryName = helper.createRestQuery(querySetup.datasourceName, "WikipediaQuery",
                    WIKIPEDIA_QUERY_URL, WIKIPEDIA_QUERY_JSON_PATH)
            helper.setTemplateOnQuery(outerQueryName, outerTemplateName)
            helper.setConverterOnQuery(outerQueryName, querySetup.converterName)

            helper.createPocketQueryPage(SPACEKEY, outerQueryName)

            dom.awaitElementPresent(".pocketquery-result .mw-parser-output", 40)
        }
    }


}