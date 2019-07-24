package de.scandio.e4.it.pqconf

import de.scandio.e4.BaseSeleniumTest
import de.scandio.e4.E4TestEnv
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.pocketquery.PocketQueryConfTestPackage
import de.scandio.e4.testpackages.pocketquery.pqconf.PocketQueryConfluenceSeleniumHelper
import org.junit.After
import org.junit.Test
import java.util.*

class PocketQueryConfluenceSeleniumTestSuite : BaseSeleniumTest() {

    val CONFLUENCE_DB_URL = "jdbc:postgresql://confluence-cluster-${E4TestEnv.APPLICATION_VERSION_DOT_FREE}-db:5432/confluence"
    val CONFLUENCE_DB_NAME = "confluence"
    val CONFLUENCE_DB_USER = "confluence"
    val CONFLUENCE_DB_PWD = "confluence"

    var pqHelper: PocketQueryConfluenceSeleniumHelper? = null
    var webConfluence: WebConfluence? = null

    init {
        this.webConfluence = webClient() as WebConfluence
        this.pqHelper = PocketQueryConfluenceSeleniumHelper(webConfluence(), webConfluence().dom)
        runPrepareActions(PocketQueryConfTestPackage())
    }

    @Test // 3.9.3
    fun testHelperRenderPocketQueryMacro() {
        val webConfluence = webConfluence()
        val pqHelper = pqHelper()
        try {
            val wikipediaTitle = "Vancouver"
            val wikipediaUrl = "https://en.wikipedia.org/w/api.php"
            val wikipediaQuery = "?action=parse&page=$wikipediaTitle&format=json"
            val wikipediaJsonPath = "$.parse.text['*']"
            val wikipediaConverter = """
                function convert(json) {
                    var parsedJsonObject = JSON.parse(json);
                    return [{ 'wikipediaContent': parsedJsonObject }];
                }
            """.trimIndent().trimLines()
            val innerTemplate = """
                    <link rel='stylesheet' href='https://en.wikipedia.org/w/load.php?debug=false&lang=en&modules=site.styles&only=styles&skin=vector'>
                    ${'$'}result.get(0).wikipediaContent
            """.trimIndent().trimLines()


            webConfluence.login()
            pqHelper.goToPocketQueryAdmin()
            val wikipediaDatasourceName = pqHelper.createRestCustomDatasource("WikipediaDS", wikipediaUrl)
            val wikipediaQueryName = pqHelper.createRestQuery(wikipediaDatasourceName,
                    "WikipediaNestedQuery", wikipediaQuery, wikipediaJsonPath)

            val wikipediaTemplateName = pqHelper.createTemplate("WikipediaTemplate", innerTemplate)
            val wikipediaConverterName = pqHelper.createConverter("WikipediaConverter", wikipediaConverter)

            val outerTemplate = "\$PocketQuery.renderPocketQueryMacro('$wikipediaQueryName')"

            pqHelper.setTemplateOnQuery(wikipediaQueryName, wikipediaTemplateName)
            pqHelper.setConverterOnQuery(wikipediaQueryName, wikipediaConverterName)

            val outerTemplateName = pqHelper.createTemplate("RenderConfluenceMacro", outerTemplate)
            val outerQueryName = pqHelper.createRestQuery(wikipediaDatasourceName, "WikipediaQuery",
                    wikipediaQuery, wikipediaJsonPath)
            pqHelper.setTemplateOnQuery(outerQueryName, outerTemplateName)
            pqHelper.setConverterOnQuery(outerQueryName, wikipediaConverterName)

            val pageTitle = "PQ 3.9.3 (${Date().time})"
            webConfluence.createDefaultPage("PQ", pageTitle)
            webConfluence.goToEditPage()
            pqHelper.insertPocketQueryMacro(outerQueryName)
            webConfluence.savePage()
            dom.awaitElementPresent(".pocketquery-result .mw-parser-output", 40)
        } catch (e: Exception) {
            shot()
            throw e
        } finally {
            shot()
        }
    }

    @Test // 3.9.4
    fun testSqlSetup() {
        try {
            webConfluence().login()
            pqHelper().goToPocketQueryAdmin()
            val queryName = pqHelper().createSqlSetup(CONFLUENCE_DB_NAME, CONFLUENCE_DB_URL, CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD)
            val defaultResultSelector = ".pocketquery-result .pocketquery-table"
            val wikipediaResultSelector = ".pocketquery-result .mw-parser-output"
            pqHelper().createPocketQueryPage(queryName, defaultResultSelector)
            pqHelper().createPocketQueryPage( queryName, defaultResultSelector, arrayListOf("dynamicload"))
            pqHelper().createWikipediaSetup()
            pqHelper().createPocketQueryPage(queryName, wikipediaResultSelector)
            pqHelper().createPocketQueryPage(queryName, wikipediaResultSelector, arrayListOf("dynamicload"))
        } catch (e: Exception) {
            shot()
            throw e
        } finally {
            shot()
        }
    }

    @After
    fun after() {
        webClient().quit()
    }

    private fun webConfluence() : WebConfluence {
        return this.webClient!! as WebConfluence
    }

    private fun pqHelper() : PocketQueryConfluenceSeleniumHelper {
        return this.pqHelper!!
    }


    fun String.trimLines() = replace("\n", "")

}