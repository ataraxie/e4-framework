package de.scandio.e4.setup

import de.scandio.e4.testpackages.pagebranching.actions.MergeBranchAction
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse

class TestMergeBranchAction : TestBase() {

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
            MergeBranchAction("PB").execute(webConfluence, restConfluence)
            assertFalse(dom.isElementPresent(".page-branching-branch-meta"), "Page Branching Metadata should be gone")
            webConfluence.takeScreenshot("test")

        } catch (e: Exception) {
            webConfluence.takeScreenshot("fail")
            throw e
        }

    }

}