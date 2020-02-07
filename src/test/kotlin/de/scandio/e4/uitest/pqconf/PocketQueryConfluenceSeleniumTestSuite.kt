package de.scandio.e4.uitest.pqconf

import de.scandio.e4.E4Env
import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.pocketquery.pqconf.PocketQueryConfluenceSeleniumHelper
import org.junit.After
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.assertEquals

/**
 * Assumptions (this will change and become generic!):
 * - MySQL confluence datasource: jdbc:mysql://localhost:3306/confluence
 * - JNDI datasource in server.xml java:comp/env/jdbc/world
 * - DB user confluence/confluence with access to both databases above
 * - Space with key "TEST"
 */
class PocketQueryConfluenceSeleniumTestSuite : BaseSeleniumTest() {

    val CONFLUENCE_DB_URL = System.getenv("CONFLUENCE_DB_URL")
    val CONFLUENCE_DB_NAME = "confluence"
    val CONFLUENCE_DB_USER = "confluence"
    val CONFLUENCE_DB_PWD = "confluence"
    val SPACEKEY = "TEST"
    val JNDI_RESOURCE_NAME = "jdbc/confluence"
    val SIMPLE_QUERY_SQL = "SELECT id, user_name FROM cwd_user"

    val DEFAULT_RESULT_SELECTOR = ".pocketquery-result .pocketquery-table"

    var pqHelper: PocketQueryConfluenceSeleniumHelper? = null

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        val restConfluence = restClient() as RestConfluence
        webConfluence().dom.defaultDuration = 15
        webConfluence().dom.defaultWaitTillPresent = 15
        this.pqHelper = PocketQueryConfluenceSeleniumHelper(webConfluence(), webConfluence().dom, SPACEKEY)
        try {
            restConfluence.createSpace(SPACEKEY, "E4 PocketQuery")
        } catch (e: Exception) {
            log.warn("Could not create space. Probably it already exists.")
        }
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
            webConfluence.createDefaultPage(SPACEKEY, pageTitle)
            webConfluence.goToEditPage()
            pqHelper.insertPocketQueryMacro(outerQueryName)
            webConfluence.savePage()
            webConfluence.dom.awaitElementPresent(".pocketquery-result .mw-parser-output", 40)
        } finally {
            dump()
            shot()
        }
    }

    @Test // 3.10.0 // PRODUCTS-1406 // test basic sql and rest setup to make sure core functionality works
    fun testSimpleSqlAndRestSetup() {
        try {
            webConfluence().login()
            pqHelper().goToPocketQueryAdmin()
            val dom = webConfluence().dom
            val queryNameSql = pqHelper().createSqlSetup(CONFLUENCE_DB_NAME, CONFLUENCE_DB_URL, CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD)
            pqHelper().createPocketQueryPage(queryNameSql)
            dom.awaitElementPresent(".pocketquery-table", 20)
            pqHelper().createPocketQueryPage(queryNameSql, arrayListOf("dynamicload"))
            dom.awaitElementPresent(".pocketquery-table", 20)
            val wikipediaResultSelector = ".pocketquery-result .mw-parser-output"
            val queryNameRest = pqHelper().createWikipediaSetup()
            pqHelper().createPocketQueryPage(queryNameRest)
            dom.awaitElementPresent(wikipediaResultSelector, 20)
            pqHelper().createPocketQueryPage(queryNameRest, arrayListOf("dynamicload"))
            dom.awaitElementPresent(wikipediaResultSelector, 20)
        } finally {
            shot()
            dump()
        }
    }

    @Test // 3.10.0 // PRODUCTS-1394 // NOTE: requires world JNDI datasource in server.xml
    fun testJndiDatasourceTest() {
        try {
            val helper = pqHelper()
            val dom = webConfluence().dom
            webConfluence().login()
            helper.goToPocketQueryAdmin()
            helper.goToAdminSection("database")
            helper.openAddEntityForm("database")
            val datasourceName = helper.createJndiDatasource("ConfluenceDsJNDI", JNDI_RESOURCE_NAME)
            helper.openEditEntityForm("database", datasourceName)
            dom.click("a.testconnection")
            dom.awaitHasText("#pocket-databases .nice-right .nice-status", "success", 10)

            val queryName = helper.createSqlQuery(datasourceName, "ConfluenceJNDI", SIMPLE_QUERY_SQL)
            pqHelper().createPocketQueryPage(queryName)
            dom.awaitElementPresent(DEFAULT_RESULT_SELECTOR, 20)
            pqHelper().createPocketQueryPage(queryName, arrayListOf("dynamicload"))
            dom.awaitElementPresent(DEFAULT_RESULT_SELECTOR, 20)
        } finally {
            dump()
            shot()
        }
    }

    @Test // 3.10.0 // PRODUCTS-841 // description field tested with all entity types
    fun testDescriptionField() {
        try {
            val helper = pqHelper()
            val dom = webConfluence().dom
            webConfluence().login()
            helper.goToPocketQueryAdmin()

            // SQL datasource
            val sqlDatasourceName = helper.createSqlDatasource("DescriptionTestDsSql",
                    CONFLUENCE_DB_URL, CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD, "SqlDatabaseDescription")
            // JNDI datasource
            val jndiDatasourceName = helper.createJndiDatasource("DescriptionTestDsJndi",
                    JNDI_RESOURCE_NAME, "JndiDatabaseDescription")
            // REST datasource
            val restDatasourceName = helper.createRestCustomDatasource("DescriptionTestDsRest",
                    "https://en.wikipedia.org/w/api.php", "RestDatabaseDescription")
            // SQL Query
            val sqlQuery = helper.createSqlQuery(sqlDatasourceName, "DescriptionTestSqlQuery", SIMPLE_QUERY_SQL,
                    "SqlQueryDescription")
            // JNDI Query
            val jndiQuery = helper.createSqlQuery(jndiDatasourceName, "DescriptionTestJndiQuery", SIMPLE_QUERY_SQL,
                    "JndiQueryDescription")
            // REST Query
            val restQuery = helper.createRestQuery(restDatasourceName, "DescriptionTestRestQuery",
                    "?action=parse&page=Vancouver&format=json", "", "RestQueryDescription")
            // Template
            val templateName = helper.createTemplate("DescriptionTestTemplate",
                    "DescriptionTestTemplate Content", "TemplateDescription")
            // Converter
            val converterName = helper.createConverter("DescriptionTestConverter",
                    "DescriptionTestConverter Content", "ConverterDescription")

            // Check everything! We invoke this before and after reload of the PQ admin to make sure things persist :)
            fun checkAll() {
                helper.openEditEntityForm("database", sqlDatasourceName)
                dom.awaitHasValue("#database-description", "SqlDatabaseDescription")
                helper.openEditEntityForm("database", jndiDatasourceName)
                dom.awaitHasValue("#database-description", "JndiDatabaseDescription")
                helper.openEditEntityForm("database", restDatasourceName)
                dom.awaitHasValue("#database-description", "RestDatabaseDescription")
                helper.openEditEntityForm("query", sqlQuery)
                dom.awaitHasValue("#query-description", "SqlQueryDescriptio")
                helper.openEditEntityForm("query", jndiQuery)
                dom.awaitHasValue("#query-description", "JndiQueryDescription")
                helper.openEditEntityForm("query", restQuery)
                dom.awaitHasValue("#query-description", "RestQueryDescription")
                helper.openEditEntityForm("template", templateName)
                dom.awaitHasValue("#template-description", "TemplateDescription")
                helper.openEditEntityForm("converter", converterName)
                dom.awaitHasValue("#converter-description", "ConverterDescription")
            }

            checkAll()

            // Reload and check it all again to make sure things persist...
            webConfluence().login()
            helper.goToPocketQueryAdmin()

            checkAll()

        } finally {
            dump()
            shot()
        }
    }

    @Test // 3.10.0 // PRODUCTS-957 // read-only support // NOTE: only data center!
    fun testReadOnlyMode() {
        log.info("Read-Only mode test. Will only be run in data center.");
        if (!E4Env.IS_DATACENTER) {
            log.info("No Data Center: skipping read-only mode test");
            return
        }

        if (Integer.parseInt(E4Env.APPLICATION_VERSION_DOT_FREE) < 6100) {
            log.info("Confluence version lower than 6.10.0. Read only mode was not available yet. Skipping test")
            return
        }

        try {
            val helper = pqHelper()
            val webConfluence = webConfluence()
            val dom = webConfluence.dom
            webConfluence().login()
            helper.goToPocketQueryAdmin()
            val datasourceName = helper.createSqlDatasource("ReadOnlyModeDS",
                    CONFLUENCE_DB_URL, CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD)
            val templateName = helper.createTemplate("ReadOnlyModeTemplate", "Template Content")
            val converterName = helper.createConverter("ReadOnlyModeConverter", "Converter Content")
            val queryName = helper.createSqlQuery(datasourceName, "ReadOnlyModeQuery", SIMPLE_QUERY_SQL)

            fun assertAllInputsDisabled(containerSelector: String) {
                val numInputs = dom.findElements("$containerSelector input").size
                val numInputsDisabled = dom.findElements("$containerSelector input[disabled]").size
                assertEquals(numInputs, numInputsDisabled)
            }

            fun checkAllEnabled(entityType: String, entityName: String) {
                helper.openEditEntityForm(entityType, entityName)
                assert(dom.findElements("#nice-form-$entityType input").isNotEmpty())
                assert(dom.findElements("#nice-form-$entityType input[disabled]").isEmpty())
                assert(dom.findElement("#nice-form-$entityType .button.submit").isDisplayed)
                assert(dom.findElement(".nice-box[data-section='$entityType'] .nice-list .nice-remove").isDisplayed)
            }

            fun checkAllDisabled(entityType: String, entityName: String) {
                helper.openEditEntityForm(entityType, entityName)
                assertAllInputsDisabled("#nice-form-$entityType")
                assert(!dom.findElement("#nice-form-$entityType .button.submit").isDisplayed)
                assert(!dom.findElement(".nice-box[data-section='$entityType'] .nice-list .nice-remove").isDisplayed)
            }

            fun checksForReadOnlyInactive() {
                helper.goToPocketQueryAdmin()
                checkAllEnabled("database", datasourceName)
                checkAllEnabled("query", queryName)
                checkAllEnabled("template", templateName)
                checkAllEnabled("converter", converterName)
                webConfluence.navigateTo("plugins/servlet/pocketquery/sysadmin/config")
                dom.awaitElementPresent(".pocketquery-admin-container.config")
                assert(dom.findElements(".pocketquery-admin-container.config input").isNotEmpty())
                assert(dom.findElements(".pocketquery-admin-container.config input[disabled]").isEmpty())
                assert(dom.findElement(".pocketquery-admin-container.config #confirm").isDisplayed)
                webConfluence.navigateTo("plugins/servlet/pocketquery/sysadmin/importexport")
                dom.awaitElementPresent(".pocketquery-admin-container.importexport")
                assert(dom.findElement("#pocketquery-import-form").isDisplayed)
                assertNoElement(".aui-message-warning.readonlymode")
                assert(dom.findElement("#pocketquery-import-form .submit").isDisplayed)
            }

            fun checksForReadOnlyActive() {
                helper.goToPocketQueryAdmin()
                checkAllDisabled("database", datasourceName)
                checkAllDisabled("query", queryName)
                checkAllDisabled("template", templateName)
                checkAllDisabled("converter", converterName)
                webConfluence.navigateTo("plugins/servlet/pocketquery/sysadmin/config")
                dom.awaitElementPresent(".pocketquery-admin-container.config")
                assertAllInputsDisabled(".pocketquery-admin-container.config")
                assertNoElement(".pocketquery-admin-container.config #confirm")
                webConfluence.navigateTo("plugins/servlet/pocketquery/sysadmin/importexport")
                dom.awaitElementPresent(".pocketquery-admin-container.importexport")
                assertNoElement("#pocketquery-import-form")
                assertOneElement(".aui-message-warning.readonlymode")
                assertNoElement("#pocketquery-import-form .submit")
            }

            checksForReadOnlyInactive()
            webConfluence.enterReadOnlyMode()
            checksForReadOnlyActive()
            webConfluence.exitReadOnlyMode()
            checksForReadOnlyInactive()
        } finally {
            dump()
            shot()
        }

    }

    @Test // 3.10.0 // PRODUCTS-957
    fun testNumberAtBeginningOfHeaders() {
        try {
            val helper = pqHelper()
            webConfluence().login()
            pqHelper().goToPocketQueryAdmin()
            val dom = webConfluence().dom
            val queryNameRest = helper.createWikipediaDatasource("WikipediaDS")
            val keyValueContainerSelector = "#database-requestheaders + .key-value-container"
            helper.openEditEntityForm("database", queryNameRest)
            dom.click("$keyValueContainerSelector .add")
            dom.insertText("$keyValueContainerSelector .key", "1numberKey")
            dom.insertText("$keyValueContainerSelector .value", "1numberValue")
            dom.click("a.testconnection")
            dom.awaitHasText("#pocket-databases .nice-right .nice-status", "success", 10)
        } finally {
            dump()
            shot()
        }
    }

    @Test // 3.10.0 // PRODUCTS-1390 // percent sign in templates
    fun testChangeTemplateAndPercentSign() {
        try {
            webConfluence().login()
            pqHelper().goToPocketQueryAdmin()
            val dom = webConfluence().dom
            val datasourceName = createConfluenceSqlDataSource()
            val queryName = pqHelper().createSqlQuery(datasourceName, "LikeQuery",
                    "SELECT id, user_name FROM cwd_user WHERE user_name LIKE :username")
            pqHelper().createPocketQueryPage(
                    queryName, listOf("dynamicload", "includechangetemplate"),
                    mapOf("username" to "%"))
            dom.awaitElementPresent(".pq-dynamic-parameter-form", 10)
            dom.awaitElementPresent(DEFAULT_RESULT_SELECTOR, 10)
        } finally {
            dump()
            shot()
        }
    }


    @Test // 3.12.0 // PQCSRV-8 // XML responses (https://help.scandio.de/documentation/x/AoBbCQ)
    fun testXmlResponses() {
        try {
            webConfluence().login()
            pqHelper().goToPocketQueryAdmin()
            val dom = webConfluence().dom
            val datasourceName = pqHelper().createRestCustomDatasource("GithubRawDataSource", "https://raw.githubusercontent.com/", "Used for testing with raw Github content")
            val queryName = pqHelper().createRestQuery(datasourceName, "GithubRawXmlQuery", "/fundapps/api-examples/master/Sample-XML/Swap.xml")
            val converterName = pqHelper().createConverter("GithubRawXmlConverter", """
                function convert(json) {
                    var parsedJsonObject = JSON.parse(json);
                    return [{Snapshot: JSON.stringify(parsedJsonObject.Snapshot)}];
                }
            """.trimIndent().trimLines(), "Used for testing with raw Github content")
            pqHelper().setConverterOnQuery(queryName, converterName)

            pqHelper().createPocketQueryPage(queryName)
            dom.awaitElementPresent(DEFAULT_RESULT_SELECTOR, 10)
            dom.awaitHasText(".pocketquery-result table tbody tr:first-child td", "Instruments")
        } finally {
            dump()
            shot()
        }
    }

    @After
    fun after() {
        webClient().quit()
    }

    private fun createConfluenceSqlDataSource(): String {
        return pqHelper().createSqlDatasource(CONFLUENCE_DB_NAME, CONFLUENCE_DB_URL, CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD)
    }

    private fun webConfluence() : WebConfluence {
        return this.webClient!! as WebConfluence
    }

    private fun pqHelper() : PocketQueryConfluenceSeleniumHelper {
        return this.pqHelper!!
    }

}