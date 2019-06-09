package de.scandio.e4.setup

import org.junit.After
import org.junit.Before
import org.junit.Test

class TestEditPage : TestBase() {

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
        } catch (e: Exception) {
            this.webConfluence.takeScreenshot("fail")
            throw e
        }

    }

}