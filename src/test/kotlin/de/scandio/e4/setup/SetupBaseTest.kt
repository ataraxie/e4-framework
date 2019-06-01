package de.scandio.e4.setup

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.util.Util
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.LoggerFactory
import java.net.URI

open abstract class SetupBaseTest {

    private val log = LoggerFactory.getLogger(javaClass)

    protected val BASE_URL = "http://confluence-cluster-6153-lb:26153/"
    protected val OUT_DIR = "/tmp/e4/out"
    protected val USERNAME = "admin"
    protected val PASSWORD = "admin"

    protected val driver: WebDriver
    protected val util: Util
    protected val dom: DomHelper
    protected val webConfluence: WebConfluence

    protected var screenshotCount = 0

    init {
        WebDriverManager.chromedriver().setup()
        val chromeOptions = ChromeOptions()
        chromeOptions.addArguments("--headless")
        this.driver = ChromeDriver(chromeOptions)
        this.driver.manage().window().setSize(Dimension(1680, 1050))
        this.util = Util()
        this.dom = DomHelper(driver, 120, 120)
        this.dom.defaultDuration = 120
        this.dom.defaultWaitTillPresent = 120
        this.dom.outDir = OUT_DIR
        this.dom.screenshotBeforeClick = true
        this.dom.screenshotBeforeInsert = true
        this.webConfluence = WebConfluence(driver, URI(BASE_URL), OUT_DIR, USERNAME, PASSWORD)
    }

    open fun shot() {
        this.screenshotCount += 1
        this.util.takeScreenshot(driver, "$OUT_DIR/$screenshotCount-confluence-data-center-setup.png")
    }

    open fun installPlugin(absoluteFilePath: String) {
        webConfluence.login()
        webConfluence.authenticateAdmin()
        println("Installing ${absoluteFilePath.split('/').last()}")
        goTo("plugins/servlet/upm")
        shot()
        dom.awaitElementPresent(".upm-plugin-list-container", 30)
        shot()
        dom.click("#upm-upload")
        println("-> Waiting for upload dialog...")
        dom.awaitElementPresent("#upm-upload-file")
        dom.findElement("#upm-upload-file").sendKeys(absoluteFilePath)
        dom.click("#upm-upload-dialog button.confirm")
        println("-> Waiting till upload is fully done...")
        dom.awaitClass("#upm-manage-container", "loading", 30)
        dom.awaitNoClass("#upm-manage-container", "loading", 30)
        println("--> SUCCESS")
    }

    open fun goTo(url: String) {
        driver.navigate().to("$BASE_URL$url")
    }

}