package de.scandio.e4.setup

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.util.Util
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import java.net.URI

class TestLogin {

    private val BASE_URL = "http://e4-test:8090/"
    private val OUT_DIR = "/tmp/e4/out"
    private val USERNAME = "admin3"
    private val PASSWORD = "admin3"

    private val driver: WebDriver
    private val util: Util
    private val dom: DomHelper
    private val webConfluence: WebConfluence

    private var screenshotCount = 0

    init {
        WebDriverManager.chromedriver().setup()
        val chromeOptions = ChromeOptions()
        chromeOptions.addArguments("--headless")
        this.driver = ChromeDriver(chromeOptions)
        this.driver.manage().window().setSize(Dimension(1680, 1050))
        this.util = Util()
        this.dom = DomHelper(driver, 120, 120)
        this.dom.defaultDuration = 5
        this.dom.defaultWaitTillPresent = 5
        this.dom.outDir = OUT_DIR
        this.dom.screenshotBeforeClick = true
        this.dom.screenshotBeforeInsert = true
        this.webConfluence = WebConfluence(driver, URI(BASE_URL), OUT_DIR, USERNAME, PASSWORD)
        this.webConfluence.dom = dom
    }

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
            this.webConfluence.takeScreenshot("failure")
            throw e
        }

    }

}