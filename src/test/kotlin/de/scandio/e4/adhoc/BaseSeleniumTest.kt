package de.scandio.e4.adhoc

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import de.scandio.e4.E4TestEnv
import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.Util
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.LoggerFactory

abstract class BaseSeleniumTest {

    val DEFAULT_DURATION = 30L //seconds
    val DEFAULT_DIMENSION = Dimension(2000, 1500)

    private val log = LoggerFactory.getLogger(javaClass)

    protected var webClient: WebClient? = null
    protected var restClient: RestClient? = null

    protected var driver: WebDriver? = null
    protected var util: Util? = null
    protected var dom: DomHelper? = null

    protected var screenshotCount = 0
    protected var dumpCount = 0

    init {
        newWebDriver()

        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerContext.getLogger("org.apache").level = Level.WARN

        setNewClients()
    }

    open fun refreshWebClient(login: Boolean = false, authenticate: Boolean = false) {
        newWebDriver()
        setNewClients()

        if (login) {
            webClient().login()
        }

        if (authenticate) {
            webClient().authenticateAdmin()
        }
    }

    fun newWebDriver() {
        WebDriverManager.chromedriver().setup()

        val chromeOptions = ChromeOptions()
        chromeOptions.addArguments("--headless")
        this.driver = ChromeDriver(chromeOptions)
        (this.driver as ChromeDriver).manage().window().size = DEFAULT_DIMENSION
        this.util = Util()

        this.dom = DomHelper(driver as ChromeDriver, DEFAULT_DURATION, DEFAULT_DURATION)
        val dom = this.dom!!
        dom.defaultDuration = DEFAULT_DURATION
        dom.defaultWaitTillPresent = DEFAULT_DURATION
        dom.outDir = E4TestEnv.OUT_DIR
        dom.screenshotBeforeClick = true
        dom.screenshotBeforeInsert = true
    }

    fun setNewClients() {
        this.webClient = E4TestEnv.newAdminTestWebClient()
        this.restClient = E4TestEnv.newAdminTestRestClient()
    }

    open fun shot() {
        this.screenshotCount += 1
        val path = webClient().takeScreenshot("$screenshotCount-selenium-test.png")
        println(path)
    }

    open fun dump() {
        this.dumpCount += 1
        val path = webClient().dumpHtml("$dumpCount-selenium-test.html")
        println(path)
    }

    open fun quit() {
        webClient().quit()
    }

    fun webClient() : WebClient {
        return this.webClient!!
    }

    fun restClient() : RestClient {
        return this.restClient!!
    }

    fun runPrepareActions(testPackage: TestPackage) {
        log.info("Running PREPARE actions")
        for (action in testPackage.setupActions) {
            try {
                log.info("Executing PREPARE action ${action.javaClass.simpleName}")
                action.execute(webClient(), restClient())
            } catch (e: Exception) {
                log.error("ERROR executing prepare action", e)
                dump()
                shot()
            }
        }
    }

    fun String.trimLines() = replace("\n", "")

}