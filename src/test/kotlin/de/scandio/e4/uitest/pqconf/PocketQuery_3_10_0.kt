package de.scandio.e4.uitest.pqconf

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Assumptions:
 * - PQ is installed and licensed
 * - Confluence has a data center license (required to test read-only mode)
 */
class PocketQuery_3_10_0 : AbstractPocketQueryConfluenceTestSuite() {

    @Test // 3.10.0 // PRODUCTS-1394 // NOTE: requires JNDI datasource with name jdbc/confluence in server.xml
    fun testJndiDatasourceTest() {
        runWithDump {
            helper.goToPocketQueryAdmin()
            helper.openAddEntityForm("database")
            val datasourceName = helper.createJndiDatasource("ConfluenceDsJNDI", JNDI_RESOURCE_NAME)
            helper.openEditEntityForm("database", datasourceName)
            dom.click("a.test-connection")
            webConfluence.awaitSuccessFlag()

            val queryName = helper.createSqlQuery(datasourceName, "ConfluenceJNDI", SIMPLE_QUERY_SQL)
            helper.createPocketQueryPage(SPACEKEY, queryName)
            dom.awaitElementPresent(DEFAULT_RESULT_SELECTOR, 20)

            // FIXME: PQCSRV-156
            // helper.createPocketQueryPage(SPACEKEY, queryName, arrayListOf("dynamicLoad"))
            // dom.awaitElementPresent(DEFAULT_RESULT_SELECTOR, 20)
        }
    }

    @Test // 3.10.0 // PRODUCTS-841 // description field tested with all entity types
    fun testDescriptionField() {
        runWithDump {
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
                    "?action=parse&page=Vancouver&format=json", "RestQueryDescription")
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

            // Check it all again to make sure things persist...
            helper.goToPocketQueryAdmin()

            checkAll()

        }
    }

    @Test // 3.10.0 // PRODUCTS-957 // read-only support // NOTE: only data center!
    fun testReadOnlyMode() {
        runWithDump {
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
                dom.expectElementDisplayed("#nice-form-$entityType .submit")
                dom.expectElementDisplayed(".nice-box[data-section='$entityType'] .nice-list .nice-remove")
            }

            fun checkAllDisabled(entityType: String, entityName: String) {
                helper.openEditEntityForm(entityType, entityName)
                // FIXME: PQCSRV-158
                // assertAllInputsDisabled("#nice-form-$entityType")
                dom.scrollToBottom("body")
                // dom.expectElementNotDisplayed("#nice-form-$entityType .submit")
                dom.expectElementNotDisplayed(".nice-box[data-section='$entityType'] .nice-list .nice-remove")
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
                dom.expectElementDisplayed(".pocketquery-admin-container.config #confirm")
                webConfluence.navigateTo("plugins/servlet/pocketquery/sysadmin/importexport")
                dom.awaitElementPresent(".pocketquery-admin-container.importexport")
                dom.expectElementDisplayed("#pocketquery-import-form")
                assertNoElement(".aui-message-warning.readonlymode")
                dom.scrollToBottom("body")
                dom.expectElementDisplayed("#pocketquery-import-form .submit")
            }

            fun checksForReadOnlyActive() {
                helper.goToPocketQueryAdmin()
                checkAllDisabled("database", datasourceName)
                checkAllDisabled("query", queryName)
                checkAllDisabled("template", templateName)
                checkAllDisabled("converter", converterName)
                webConfluence.navigateTo("plugins/servlet/pocketquery/sysadmin/config")
                dom.awaitElementPresent(".pocketquery-admin-container.config")
                // FIXME: PQCSRV-158
                // assertAllInputsDisabled(".pocketquery-admin-container.config")
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
        }
    }

    @Test // 3.10.0 // PRODUCTS-957
    fun testNumberAtBeginningOfHeaders() {
        runWithDump {
            helper.goToPocketQueryAdmin()
            val queryNameRest = helper.createWikipediaDatasource("WikipediaDS")
            val keyValueContainerSelector = "#database-requestheaders + .key-value-container"
            helper.openEditEntityForm("database", queryNameRest)
            dom.click("$keyValueContainerSelector .add")
            dom.insertText("$keyValueContainerSelector .key", "1numberKey")
            dom.insertText("$keyValueContainerSelector .value", "1numberValue")
            dom.click("a.test-connection")
            webConfluence.awaitSuccessFlag()
            helper.submitForm("database")
        }
    }

    @Test // 3.10.0 // PRODUCTS-1390 // percent sign in templates
    fun testChangeTemplateAndPercentSign() {
        runWithDump {
            helper.goToPocketQueryAdmin()
            val datasourceName = helper.createSqlDatasource(CONFLUENCE_DB_NAME, CONFLUENCE_DB_URL, CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD)
            val queryName = helper.createSqlQuery(datasourceName, "LikeQuery",
                    "SELECT id, user_name FROM cwd_user WHERE user_name LIKE :username")
            helper.createPocketQueryPage(
                    SPACEKEY,
                    queryName, listOf("dynamicLoad", "includeChangeTemplate"),
                    mapOf("username" to "%"))
            dom.awaitElementPresent(".pq-dynamic-parameter-form", 10)
            dom.awaitElementPresent(DEFAULT_RESULT_SELECTOR, 10)
        }
    }

}