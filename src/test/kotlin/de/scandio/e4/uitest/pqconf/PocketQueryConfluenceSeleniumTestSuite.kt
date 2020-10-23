package de.scandio.e4.uitest.pqconf

import org.junit.Test
import kotlin.test.assertEquals

class PocketQueryConfluenceSeleniumTestSuite : AbstractPocketQueryConfluenceTestSuite() {

    @Test // Test basic SQL setup
    fun test_simple_sql_setup() {
        runWithDump {
            helper.goToPocketQueryAdmin()
            val queryNameSql = helper.createSqlSetup(CONFLUENCE_DB_NAME, CONFLUENCE_DB_URL,
                    CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD)
            helper.createPocketQueryPage(SPACEKEY, queryNameSql)
            dom.awaitElementPresent(".pocketquery-table", 20)
            helper.createPocketQueryPage(SPACEKEY, queryNameSql, arrayListOf("dynamicLoad"))
            dom.awaitElementPresent(".pocketquery-table", 20)
        }
    }

    @Test
    fun test_simple_rest_setup() {
        runWithDump {
            val querySetup = helper.createWikipediaSetup()
            helper.createPocketQueryPage(SPACEKEY, querySetup.queryName)
            dom.awaitElementPresent(WIKIPEDIA_RESULT_SELECTOR, 20)
            helper.createPocketQueryPage(SPACEKEY, querySetup.queryName, arrayListOf("dynamicload"))
            dom.awaitElementPresent(WIKIPEDIA_RESULT_SELECTOR, 20)
        }
    }

    @Test
    fun test_create_and_remove_datasource() {
        runWithDump {
            helper.goToPocketQueryAdmin()
            val dsName = helper.createSqlDatasource("TempDatasource", CONFLUENCE_DB_URL, CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD)
            dom.awaitElementPresent("li[data-displayname=\"$dsName\"]")
            dom.awaitMilliseconds(500)
            dom.click("li[data-displayname=\"$dsName\"] a.nice-remove")
            dom.awaitMilliseconds(500)
            dom.click("#pq-dialog-confirm button.confirm")
            dom.awaitMilliseconds(500)
            dom.expectElementNotPresent("li[data-displayname=\"$dsName\"]")
        }

    }

    @Test
    fun test_admin_menu_button() {
        runWithDump {
            dom.click("#admin-menu-link")
            dom.awaitSeconds(1)
            dom.click("#admin-menu-link-content[aria-hidden=\"false\"] #pocketquery-admin-link")
            dom.awaitElementPresent(".pocketquery-view-admin")
            dom.awaitSeconds(1)
        }
    }

    @Test
    fun test_footer_links() {
        runWithDump {
            // FIXME: make asserts work and possibly add scroll to bottom such that fotter is in view
            helper.goToPocketQueryAdmin()
            dom.click("#pocket-footer a[href*=\"documentation\"]")
            dom.awaitSeconds(2)
//        assertTrue(driver.currentUrl.contains("/pocketquery-server-documentation"))
            helper.goToPocketQueryAdmin()
            dom.click("#pocket-footer a[href*=\"support\"]")
            dom.awaitSeconds(2)
//        assertTrue(driver.currentUrl.contains("/support"))
            helper.goToPocketQueryAdmin()
            dom.click("#pocket-footer a[href*=\"examples\"]")
            dom.awaitSeconds(2)
//        assertTrue(driver.currentUrl.contains("/pocketquery-server-examples"))
        }

    }

    @Test
    fun test_admin_links() {
        runWithDump {
            val sectionSelector = "#section-menuheading-pocketquery-section"
            webConfluence.goToConfluenceAdmin()

            dom.expectElementPresent(sectionSelector)
            assertEquals("pocketquery", dom.findElement(sectionSelector).text.toLowerCase())

            val items = dom.findElements("#section-pocketquery-section li")
            assertEquals(2, items.size)

            assertEquals("Configuration", items[0].text)
            assertEquals("Import/Export", items[1].text)

            dom.awaitSeconds(1)
            dom.click("#section-pocketquery-section li:first-child")
            dom.awaitElementPresent("input#editorgroup")
            dom.awaitSeconds(1)
            dom.click(".buttons #confirm")
            dom.awaitElementPresent(".aui-message-success p")
            dom.awaitSeconds(1)
            assertEquals("Configuration saved successfully!", dom.findElement(".aui-message-success p").text)

            dom.click("#section-pocketquery-section li:nth-child(2)")
            dom.awaitElementPresent("#pocketquery-import-form input[type=\"file\"]")
            dom.awaitSeconds(1)
        }

    }

    @Test
    fun test_dynamic_parameter_form() {
        runWithDump {
            helper.goToPocketQueryAdmin()
            val statement = "SELECT CONTENTID, TITLE FROM CONTENT WHERE TITLE = :Title"
            val dsName = helper.createConfluenceDatasource("ConfluenceSql")
            val queryName = helper.createSqlQuery(dsName, "Content by Title", statement)
            val title1 = webConfluence.createPageAndSave(SPACEKEY, "One test_dynamic_parameter_form")
            val title2 = webConfluence.createPageAndSave(SPACEKEY, "One test_dynamic_parameter_form")
            webConfluence.createPageKeepOpen(SPACEKEY, "TEST test_dynamic_parameter_form")
            helper.insertPocketQueryMacro(queryName, arrayListOf("allowGetParams", "includeChangeTemplate"),
                    mapOf("Title" to title1))
            webConfluence.savePageOrBlogPost()
            dom.awaitElementPresentByXpath("//td[contains(.,'${title1}')]")
            dom.insertText("#pq_Title", title2, true)
            dom.click(".pq-change-button")
            dom.awaitElementPresentByXpath("//td[contains(.,'${title2}')]")

            webConfluence.createPageKeepOpen(SPACEKEY, "TEST test_dynamic_parameter_form")
            helper.insertPocketQueryMacro(queryName, arrayListOf("dynamicLoad", "includeChangeTemplate"),
                    mapOf("Title" to title1))
            webConfluence.savePageOrBlogPost()

            dom.awaitElementPresentByXpath("//td[contains(.,'${title1}')]")
            dom.insertText("#pq_Title", title2, true)
            dom.click(".pq-change-button")
            dom.awaitElementPresentByXpath("//td[contains(.,'${title2}')]")

        }
    }

}