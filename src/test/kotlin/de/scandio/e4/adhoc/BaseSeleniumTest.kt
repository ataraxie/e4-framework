package de.scandio.e4.adhoc

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import de.scandio.e4.E4Env
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.Util
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory

abstract class BaseSeleniumTest {

    val DEFAULT_DURATION = 30L //seconds
    val DEFAULT_DIMENSION = Dimension(2000, 1500)

    private val log = LoggerFactory.getLogger(javaClass)

    protected var webClient: WebClient? = null
    protected var restClient: RestClient? = null

    protected var driver: WebDriver? = null
    protected var util: Util? = null

    protected var screenshotCount = 0
    protected var dumpCount = 0

    init {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerContext.getLogger("org.apache").level = Level.WARN

        setNewClients()
    }

    open fun refreshWebClient(login: Boolean = false, authenticate: Boolean = false) {
        setNewClients()

        if (login) {
            webClient().login()
        }

        if (authenticate) {
            webClient().authenticateAdmin()
        }
    }

    fun setNewClients() {
        this.webClient = E4Env.newAdminTestWebClient()
        this.restClient = E4Env.newAdminTestRestClient()
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