package de.scandio.e4.setup

import de.scandio.e4.testpackages.pagebranching.actions.CreateOverviewPageAction
import de.scandio.e4.testpackages.pagebranching.actions.MergeBranchAction
import org.junit.After
import org.junit.Before
import org.junit.Test

class TestCreateOverviewPageAction : TestBase() {

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
            CreateOverviewPageAction("PB").execute(webConfluence, restConfluence)
            webConfluence.takeScreenshot("test")

        } catch (e: Exception) {
            this.webConfluence.takeScreenshot("fail")
            throw e
        }

    }

}