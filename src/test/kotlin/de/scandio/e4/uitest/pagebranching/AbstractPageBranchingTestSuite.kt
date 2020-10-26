package de.scandio.e4.uitest.pagebranching

import de.scandio.e4.E4Env
import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import de.scandio.e4.testpackages.livelytheme.LivelyThemeSeleniumHelper
import de.scandio.e4.testpackages.pagebranching.PageBranchingRestHelper
import de.scandio.e4.testpackages.pagebranching.PageBranchingSeleniumHelper
import de.scandio.e4.testpackages.pocketquery.pqconf.PocketQueryConfluenceSeleniumHelper
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.util.*

@TestInstance(Lifecycle.PER_CLASS)
open class AbstractPageBranchingTestSuite : BaseSeleniumTest() {

    companion object {
        @JvmStatic
        var webConfluence = webClient as WebConfluence
        @JvmStatic
        var restConfluence = restClient as RestConfluence
        @JvmStatic
        var webHelper = PageBranchingSeleniumHelper(webConfluence)
        @JvmStatic
        var restHelper = PageBranchingRestHelper(restConfluence)

        val SPACEKEY = if (E4Env.PREPARATION_RUN) "E4PB${Date().time}" else "E4PB1603700919427"
        val SPACENAME = "E4 Page Branching"

        @AfterClass
        @JvmStatic internal fun afterAll() {
            webClient.quit()
        }
    }

    open fun expectPageInOverviewTable(pageId: Number) {
        dom.expectElementPresent("#pagebranching-overview-table tr[data-page-id=\"$pageId\"]")
    }

}