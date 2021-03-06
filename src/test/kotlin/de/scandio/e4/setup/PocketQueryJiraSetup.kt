package de.scandio.e4.setup

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.web.WebJira
import de.scandio.e4.testpackages.pocketquery.PocketQuerySeleniumHelper
import de.scandio.e4.testpackages.pocketquery.pqconf.PocketQueryJiraTestPackage
import de.scandio.e4.testpackages.pocketquery.pqjira.PocketQueryJiraSeleniumHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory

open class PocketQueryJiraSetup : BaseSeleniumTest() {

    private val log = LoggerFactory.getLogger(javaClass)

    private val webJira: WebJira = webClient as WebJira

    private val PQ_JIRA_LICENSE = System.getenv("E4_PQ_LICENSE")
    private val pqHelper: PocketQuerySeleniumHelper = PocketQueryJiraSeleniumHelper(webJira)

    @Before
    fun before() {

    }

    @After
    fun tearDown() {
        driver.quit()
    }

    @Test
    fun test() {
        try {
            for (action in PocketQueryJiraTestPackage().setupActions) {
                log.info("Executing action {{}}", action.javaClass.simpleName)
                action.execute(webClient, restClient)
            }
        } catch (e: Exception) {
            shot()
            dump()
            throw e
        } finally {
            webJira.quit()
        }
    }

}