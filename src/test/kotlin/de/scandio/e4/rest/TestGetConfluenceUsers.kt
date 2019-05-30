package de.scandio.e4.rest

import de.scandio.e4.worker.confluence.rest.RestConfluence
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


/*
Search/Replace for Java export from Selenium Chrome addon:

driver.findElement\(By.id\("(.*)"\)\).sendKeys\("(.*)"\)
dom.insertText("#$1", "$2")


driver.findElement\(By.id\("(.*)"\)\)\.click\(\)
dom.click("#$1")
 */

class TestGetConfluenceUsers {

    private val BASE_URL = "http://e4-test:8090/"
    private val OUT_DIR = "/tmp/e4/out"
    private val USERNAME = "admin"
    private val PASSWORD = "admin"

    private val restConfluence = RestConfluence(BASE_URL, USERNAME, PASSWORD)


    @Before
    fun before() {

    }

    @After
    fun tearDown() {

    }

    @Test
    fun test() {
        val confluenceUsers = restConfluence.confluenceUsers
        println(confluenceUsers)
        assertEquals(confluenceUsers.get(0), "admin")
    }

}