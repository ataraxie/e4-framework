package de.scandio.e4.setup

import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TestAddSpacePermission : TestBase() {

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
            val groupname = "confluence-users"
            val permKey = "removepage"
            val shallBePermitted = true
            val selector = ".permissionCell[data-permission='$permKey'][data-permission-group='$groupname'][data-permission-set='$shallBePermitted']"
            webConfluence.login()
            webConfluence.addSpaceGroupPermission("TEST", groupname, permKey, shallBePermitted)
            dom.awaitSeconds(4)
            assertTrue(dom.isElementPresent(selector))
        } catch (e: Exception) {
            this.webConfluence.takeScreenshot("fail")
            throw e
        }
    }


}