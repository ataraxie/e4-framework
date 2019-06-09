package de.scandio.e4.setup

import org.junit.After
import org.junit.Before
import org.junit.Test

class TestInsertMultiplePageBranchingMacros : TestBase() {

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
            webConfluence.login()
            webConfluence.goToPage("TEST", "Test Page")
            webConfluence.goToEditPage()
            webConfluence.insertMacro("page-branching-overview", "page branching")
            webConfluence.insertMacro("page-branching-overview", "page branching")
            webConfluence.insertMacro("page-branching-overview", "page branching")
        } catch (e: Exception) {
            this.webConfluence.takeScreenshot("fail")
            throw e
        }

    }

}