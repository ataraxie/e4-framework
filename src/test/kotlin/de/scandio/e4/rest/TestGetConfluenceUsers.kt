package de.scandio.e4.rest

import de.scandio.e4.E4TestEnv
import de.scandio.e4.clients.rest.RestConfluence
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals


/*
Search/Replace for Java export from Selenium Chrome addon:

driver.findElement\(By.id\("(.*)"\)\).sendKeys\("(.*)"\)
dom.insertText("#$1", "$2")


driver.findElement\(By.id\("(.*)"\)\)\.click\(\)
dom.click("#$1")
 */

class TestGetConfluenceUsers {

    private val log = LoggerFactory.getLogger(javaClass)

    private val restConfluence = E4TestEnv.newAdminTestRestClient() as RestConfluence

    @Before
    fun before() {

    }

    @After
    fun tearDown() {

    }

    @Test
    fun test() {
        val confluenceUsers = restConfluence.usernames
        log.info(confluenceUsers.toString())
        assertEquals(confluenceUsers.get(0), "admin")
    }

}