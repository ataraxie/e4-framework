package de.scandio.e4.uitest.pqconf

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
            helper.goToPocketQueryAdmin()
            val querySetup = helper.createWikipediaSetup()

            // FIXME: this is currently not working!
            val queryKey = dom.findElement("li[data-displayname=\"${querySetup.queryName}\"]").getAttribute("data-name")
            val outerTemplate = "\$PocketQuery.renderPocketQueryMacro('${queryKey}')"

            helper.setTemplateOnQuery(querySetup.queryName, querySetup.templateName)
            helper.setConverterOnQuery(querySetup.queryName, querySetup.converterName)

            val outerTemplateName = helper.createTemplate("RenderConfluenceMacro", outerTemplate)
            val outerQueryName = helper.createRestQuery(querySetup.datasourceName, "WikipediaQuery",
                    WIKIPEDIA_QUERY_URL)
            helper.setTemplateOnQuery(outerQueryName, outerTemplateName)
            helper.setConverterOnQuery(outerQueryName, querySetup.converterName)

            helper.createPocketQueryPage(SPACEKEY, outerQueryName)

            dom.awaitElementPresent(WIKIPEDIA_RESULT_SELECTOR, 40)
        }
    }


}