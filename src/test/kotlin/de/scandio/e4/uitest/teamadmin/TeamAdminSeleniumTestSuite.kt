package de.scandio.e4.uitest.teamadmin

import de.scandio.e4.E4Env
import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.pocketquery.pqconf.PocketQueryConfluenceSeleniumHelper
import de.scandio.e4.worker.util.WorkerUtils
import org.junit.After
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.test.assertEquals

/**
 * Assumptions (this will change and become generic!):
 * - Team Admin global configuration has been set in the following way:
 *   - All checkboxes are enabled
 *   - A default group "spacekey-teamadmin-testgroup" with only VIEW access is configured
 */
class TeamAdminSeleniumTestSuite : BaseSeleniumTest() {

    private val log = LoggerFactory.getLogger(javaClass)
    private val START_TIME = Date().time
    private val SPACEKEY = "TA"
    private val SPACENAME = "E4 Team Admin"
//    private val allSpaceKeys: List<String>

    init {
        val restConfluence = restClient() as RestConfluence
        webConfluence().dom.defaultDuration = 15
        webConfluence().dom.defaultWaitTillPresent = 15
        try {
            restConfluence.createSpace(SPACEKEY, SPACENAME)
        } catch (e: Exception) {
            log.warn("Could not create space. Probably it already exists.")
        }
//        this.allSpaceKeys = restConfluence.getAllSpaceKeys()
    }

    fun getGroupButtonIdSelector(spaceKey: String, groupname: String): String {
        return "#spaceadminedit-${spaceKey.toLowerCase()}-${groupname}"
    }

    fun createSpaceGroupRandomPermissions(spaceKey: String, groupname: String) {
        val webConfluence = webConfluence()
        val dom = webConfluence.dom
        webConfluence.login()
        webConfluence.navigateTo("/spaces/spacepermissions.action?key=${spaceKey}")
        dom.click("#spaceadmin-create-group-button")
        dom.insertText("#spaceadmin-create-group-name", groupname)
        val checkboxes = dom.findElements(".spaceadmin-permission-checkbox:not(#spaceadmin-permission-checkbox-all-view)")
        for (checkbox in checkboxes) {
            if (WorkerUtils.getRandomItem(listOf(1,2,3)) == 3) { // every third case
                dom.click(checkbox)
                dom.awaitMilliseconds(100)
            }
        }
        dom.click("#create-dialog-submit-button")
        dom.awaitElementClickable(getGroupButtonIdSelector(spaceKey, groupname))
    }

    @Test
    fun testAddRandomSpaceGroup() {
        val newGroupName = "e4${Date().time}"
        try {
            createSpaceGroupRandomPermissions(SPACEKEY, newGroupName)
        } finally {
            shot()
        }
    }

//    @Test - doesn't work yet because select2 search field handling is not implemented yet for Selenium
    fun testAddGroupMember() {
        val webConfluence = webConfluence()
        val dom = webConfluence.dom
        val newGroupName = "e4${Date().time}"
        try {
            val buttonId = getGroupButtonIdSelector(SPACEKEY, newGroupName)
            createSpaceGroupRandomPermissions(SPACEKEY, newGroupName)
            dom.click(buttonId)
            dom.awaitSeconds(2)
        } finally {
            shot()
        }
    }

    @After
    fun after() {
        webClient().quit()
    }

    @Deprecated("Use field instead", ReplaceWith("webConfluence"))
    private fun webConfluence() : WebConfluence {
        return webClient as WebConfluence
    }

}