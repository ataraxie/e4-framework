package de.scandio.e4.setup

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.enjoy.wait
import de.scandio.e4.util.Util
import de.scandio.e4.worker.confluence.rest.RestConfluence
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.WebElement
import java.awt.SystemColor.window
import java.util.concurrent.TimeUnit


/*
Search/Replace for Java export from Selenium Chrome addon:

driver.findElement\(By.id\("(.*)"\)\).sendKeys\("(.*)"\)
dom.insertText("#$1", "$2")


driver.findElement\(By.id\("(.*)"\)\)\.click\(\)
dom.click("#$1")
 */

class FindPages {

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
        print(restConfluence.findPages(10))
    }

}