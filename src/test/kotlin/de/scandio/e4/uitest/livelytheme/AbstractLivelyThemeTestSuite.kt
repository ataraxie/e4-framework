package de.scandio.e4.uitest.livelytheme

import de.scandio.e4.E4Env
import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import de.scandio.e4.testpackages.livelytheme.LivelyThemeSeleniumHelper
import de.scandio.e4.testpackages.pocketquery.pqconf.PocketQueryConfluenceSeleniumHelper
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.util.*

// REQUIRES:
// - Lively Theme installed
// - Confluence user admin/admin (if not configured differently with envvars)
//
// If you want the setup to run, set E4_PREPARATION_RUN envvar to true.
@TestInstance(Lifecycle.PER_CLASS)
open class AbstractLivelyThemeTestSuite : BaseSeleniumTest() {

    companion object {
        @JvmStatic
        var webConfluence = webClient as WebConfluence
        @JvmStatic
        var restConfluence = restClient as RestConfluence
        @JvmStatic
        var helper = LivelyThemeSeleniumHelper(webConfluence)

        val SPACEKEY = if (E4Env.PREPARATION_RUN) "E4LT${Date().time}" else "LT"
        val SPACENAME = "E4 Lively Theme"

        @BeforeClass
        @JvmStatic internal open fun beforeAll() {
            if (E4Env.PREPARATION_RUN) {
                runWithDump {
                    restConfluence.createSpace(SPACEKEY, SPACENAME)

                    webConfluence.login()
                    helper.setLivelyThemeGlobally()
                }
            }
        }
        @AfterClass
        @JvmStatic internal open fun afterAll() {
            webClient.quit()
        }

    }


    open fun expectToggleVisible(elementKey: String) {
        dom.expectElementPresent("#tabs-$elementKey.active-pane")
    }

    open fun expectToggleNotVisible(elementKey: String) {
        dom.expectElementNotPresent("#tabs-$elementKey.active-pane")
    }

    open fun expectScreenshotVisible(elementKey: String) {
        dom.expectElementPresent("#tabs-$elementKey.active-pane .screenshot[data-element=\"$elementKey\"]")
    }

    open fun expectTabActive(key: String) {
        dom.expectElementPresent(".active-tab a[href=\"#tabs-$key\"]")
    }

    open fun expectTabsAreInPosition(keys: ArrayList<String>) {
        var index = 0
        keys.forEach {
            index++
            dom.expectElementPresent(".menu-item:nth-child(${index}) a[href=\"#tabs-${it}\"]")
        }
    }


}