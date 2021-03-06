package de.scandio.e4.adhoc

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import de.scandio.e4.E4Env
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.Util
import org.openqa.selenium.Dimension
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import java.awt.Color
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

abstract class BaseSeleniumTest {

    companion object {
        @JvmStatic var webClient = E4Env.newAdminTestWebClient()
        @JvmStatic var restClient = E4Env.newAdminTestRestClient()

        var screenshotCount = 0
        var dumpCount = 0

        @JvmStatic
        fun runWithDump(block: () -> Unit) {
            try {
                block()
            } finally {
                shot()
                dump()
            }
        }

        @JvmStatic
        fun shot() {
            screenshotCount += 1
            val path = webClient.takeScreenshot("$screenshotCount-selenium-test.png")
            println(path)
        }

        @JvmStatic
        fun dump() {
            dumpCount += 1
            val path = webClient.dumpHtml("$dumpCount-selenium-test.html")
            println(path)
        }

        @JvmStatic
        fun setNewClients() {
            webClient = E4Env.newAdminTestWebClient()
            restClient = E4Env.newAdminTestRestClient()
        }

    }

    val DEFAULT_DURATION = 30L //seconds
    val DEFAULT_DIMENSION = Dimension(2000, 1500)

    private val log = LoggerFactory.getLogger(javaClass)

    protected var dom = webClient.domHelper

    protected var driver: WebDriver = webClient.webDriver
    protected var util: Util = Util()

    protected var screenshotCount = 0
    protected var dumpCount = 0

    init {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerContext.getLogger("org.apache").level = Level.WARN
    }

    open fun refreshWebClient(login: Boolean = false, authenticate: Boolean = false) {
        setNewClients()

        if (login) {
            webClient.login()
        }

        if (authenticate) {
            webClient.authenticateAdmin()
        }
    }

    open fun executePrepare(testPackage: TestPackage) {
        for (action in testPackage.setupActions) {
            action.execute(webClient(), restClient())
        }
    }

    open fun executeVirtualUser(virtualUser: VirtualUser) {
        for (action in virtualUser.actions) {
            action.execute(webClient(), restClient())
        }
    }

    open fun quit() {
        webClient().quit()
    }

    // FIXME: THESE CAN NOW BE REMOVED AND CLASS FIELD CAN JUST BE USED!
    @Deprecated("Use field webClient instead", ReplaceWith("this.webClient"))
    fun webClient() : WebClient {
        return webClient
    }

    @Deprecated("Use field restClient instead", ReplaceWith("this.restClient"))
    fun restClient() : RestClient {
        return restClient
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

    protected fun assertNoElement(selector: String) {
        assertFailsWith<NoSuchElementException>{webClient().domHelper.findElement(selector)}
    }

    protected fun assertOneElement(selector: String) {
        assertEquals(webClient().domHelper.findElements(selector).size, 1)
    }

    protected fun assertNumElements(num: Int, selector: String) {
        assertEquals(num, webClient().domHelper.findElements(selector).size)
    }

    protected fun assertHasContent(selector: String, content: String) {
        assertTrue(webClient().domHelper.findElement(selector).text.contains(content))
    }

    protected fun assertHasStyles(selector: String, styles: Map<String, String>) {
        val elem = webClient().domHelper.findElement(selector)
        for ((cssKey, cssValue) in styles) {
            assertTrue(elem.getCssValue(cssKey).contains(cssValue))
        }
    }

    protected fun assertAttributeContains(selector: String, attrName: String, attrValue: String) {
        val elem = webClient().domHelper.findElement(selector)
        assertTrue(elem.getAttribute(attrName).contains(attrValue))
    }

    protected fun assertBackgroundColor(selector: String, hexColor: String) {
        val elem = webClient().domHelper.findElement(selector)
        val background = elem.getCssValue("background")
        if (background.contains("rgb(")) {
            val color = Color.decode(hexColor)
            val regex = Regex(".*rgb\\(${color.red},\\s?${color.green},\\s?${color.blue}\\).*")
            assertTrue(background.matches(regex))
        } else {
            assertEquals(hexColor, background)
        }
    }

    fun String.trimLines() = replace("\n", "")

}