package de.scandio.e4.rest

import de.scandio.e4.worker.confluence.rest.RestConfluence
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import kotlin.test.assertTrue


/*
Search/Replace for Java export from Selenium Chrome addon:

driver.findElement\(By.id\("(.*)"\)\).sendKeys\("(.*)"\)
dom.insertText("#$1", "$2")


driver.findElement\(By.id\("(.*)"\)\)\.click\(\)
dom.click("#$1")
 */

class TestFindPages {

    private val log = LoggerFactory.getLogger(javaClass)

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
        val pageIds = restConfluence.findPages(10)
        log.info("{{}}", pageIds)
        assertTrue(pageIds.size > 0)
    }

}