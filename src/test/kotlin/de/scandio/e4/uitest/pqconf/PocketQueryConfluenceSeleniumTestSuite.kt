package de.scandio.e4.uitest.pqconf

import org.junit.Test

/**
 * Assumptions (this will change and become generic!):
 * - MySQL confluence datasource: jdbc:mysql://localhost:3306/confluence
 * - JNDI datasource in server.xml java:comp/env/jdbc/world
 * - DB user confluence/confluence with access to both databases above
 * - Space with key "TEST"
 */
class PocketQueryConfluenceSeleniumTestSuite : AbstractPocketQueryConfluenceTestSuite() {

    @Test // Test basic SQL setup
    fun testSimpleSqlSetup() {
        try {
            webConfluence.login()
            helper.goToPocketQueryAdmin()
            val queryNameSql = helper.createSqlSetup(CONFLUENCE_DB_NAME, CONFLUENCE_DB_URL, CONFLUENCE_DB_USER, CONFLUENCE_DB_PWD)
            helper.createPocketQueryPage(SPACEKEY, queryNameSql)
            dom.awaitElementPresent(".pocketquery-table", 20)
            helper.createPocketQueryPage(SPACEKEY, queryNameSql, arrayListOf("dynamicload"))
            dom.awaitElementPresent(".pocketquery-table", 20)
            val wikipediaResultSelector = ".pocketquery-result .mw-parser-output"
            val querySetup = helper.createWikipediaSetup()
            helper.createPocketQueryPage(SPACEKEY, querySetup.queryName)
            dom.awaitElementPresent(wikipediaResultSelector, 20)
            helper.createPocketQueryPage(SPACEKEY, querySetup.queryName, arrayListOf("dynamicload"))
            dom.awaitElementPresent(wikipediaResultSelector, 20)
        } finally {
            shot()
            dump()
        }
    }

}