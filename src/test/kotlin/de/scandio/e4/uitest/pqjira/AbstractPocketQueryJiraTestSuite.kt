package de.scandio.e4.uitest.pqjira

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebJira
import de.scandio.e4.testpackages.pocketquery.pqconf.PocketQueryConfluenceSeleniumHelper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
open class AbstractPocketQueryJiraTestSuite() : BaseSeleniumTest() {

    companion object {
        @JvmStatic
        var webJira = webClient as WebJira
        @JvmStatic
        var restConfluence = restClient as RestConfluence
        @JvmStatic
        var helper = PocketQueryConfluenceSeleniumHelper(webJira)
    }

}