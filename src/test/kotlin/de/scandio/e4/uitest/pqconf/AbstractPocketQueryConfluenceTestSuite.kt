package de.scandio.e4.uitest.pqconf

import de.scandio.e4.E4Env
import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import de.scandio.e4.testpackages.pocketquery.PocketQuerySeleniumHelper
import de.scandio.e4.testpackages.pocketquery.pqconf.PocketQueryConfluenceSeleniumHelper
import de.scandio.e4.worker.interfaces.WebClient
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.slf4j.LoggerFactory
import java.util.*

@TestInstance(Lifecycle.PER_CLASS)
open class AbstractPocketQueryConfluenceTestSuite() : BaseSeleniumTest() {

    companion object {
        @JvmStatic
        var webConfluence = webClient as WebConfluence
        @JvmStatic
        var restConfluence = restClient as RestConfluence
        @JvmStatic
        var helper = PocketQueryConfluenceSeleniumHelper(webConfluence)

        val SPACEKEY = if (E4Env.PREPARATION_RUN) "E4PQ${Date().time}" else "PQ"
        val SPACENAME = "E4 PocketQuery"
        val SIMPLE_QUERY_SQL = "SELECT id, user_name FROM cwd_user"
        val DEFAULT_RESULT_SELECTOR = ".pocketquery-result .pocketquery-table"
        val JNDI_RESOURCE_NAME = "jdbc/confluence"

        val CONFLUENCE_DB_URL = System.getenv("CONFLUENCE_DB_URL")
        val CONFLUENCE_DB_NAME = "confluence"
        val CONFLUENCE_DB_USER = "confluence"
        val CONFLUENCE_DB_PWD = "confluence"

        @BeforeClass
        @JvmStatic internal fun beforeAll() {
            if (E4Env.PREPARATION_RUN) {
                runWithDump {
                    restConfluence.createSpace(SPACEKEY, SPACENAME)
                }
            }
        }
        @AfterClass
        @JvmStatic internal fun afterAll() {
            webClient.quit()
        }

    }

}