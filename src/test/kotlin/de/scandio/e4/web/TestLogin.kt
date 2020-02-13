package de.scandio.e4.setup

import org.junit.After
import org.junit.Before
import org.junit.Test

class TestLogin: TestBase() {

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
            this.webConfluence.login()
        } catch (e: Exception) {
            this.webConfluence.takeScreenshot("fail")
            throw e
        }

    }

}