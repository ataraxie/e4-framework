package de.scandio.e4.uitest.pqjira

import de.scandio.e4.clients.web.WebJira
import org.junit.After
import org.junit.Test

class PocketQueryJiraSeleniumTestSuite : AbstractPocketQueryJiraTestSuite() {

    val JIRA_DB_URL = "jdbc:mysql://localhost:3306/jira"
    val JIRA_DB_NAME = "jira"
    val JIRA_DB_USER = "jira"
    val JIRA_DB_PASSWORD = "jira"

    @Test // 1.3.1
    fun testQueryInTab() {
        try {
            webJira.login()
            helper.goToPocketQueryAdmin()
            helper.createSqlSetup(JIRA_DB_NAME, JIRA_DB_URL, JIRA_DB_USER, JIRA_DB_PASSWORD)
        } catch (e: Exception) {
            shot()
            throw e
        } finally {
            shot()
        }
    }

    @After
    fun after() {
        webClient.quit()
    }

    @Deprecated("Use field instead", ReplaceWith("webJira"))
    private fun webJira() : WebJira {
        return webClient as WebJira
    }

}