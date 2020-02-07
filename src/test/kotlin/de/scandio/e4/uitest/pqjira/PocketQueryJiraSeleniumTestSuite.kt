package de.scandio.e4.uitest.pqjira

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.web.WebJira
import de.scandio.e4.testpackages.pocketquery.pqjira.PocketQueryJiraSeleniumHelper
import org.junit.After
import org.junit.Test

class PocketQueryJiraSeleniumTestSuite : BaseSeleniumTest() {

    val JIRA_DB_URL = "jdbc:mysql://localhost:3306/jira"
    val JIRA_DB_NAME = "jira"
    val JIRA_DB_USER = "jira"
    val JIRA_DB_PASSWORD = "jira"
    val JNDI_RESOURCE_NAME = "jdbc/jira"
    val SIMPLE_QUERY_SQL = "SELECT id, user_name FROM cwd_user"


    var pqHelper: PocketQueryJiraSeleniumHelper? = null
    var webJira: WebJira? = null

    init {
        this.webJira = webClient() as WebJira
        this.pqHelper = PocketQueryJiraSeleniumHelper(webJira(), webJira().dom)
        //runPrepareActions(PocketQueryJiraTestPackage())
    }

    @Test // 1.3.1
    fun testQueryInTab() {
        val webJira = webClient() as WebJira
        val pqHelper = pqHelper()
        try {
            webJira.login()
            pqHelper.goToPocketQueryAdmin()
            pqHelper.createSqlSetup(JIRA_DB_NAME, JIRA_DB_URL, JIRA_DB_USER, JIRA_DB_PASSWORD)
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

    private fun pqHelper() : PocketQueryJiraSeleniumHelper {
        return this.pqHelper!!
    }

    private fun webJira() : WebJira {
        return this.webClient!! as WebJira
    }

}