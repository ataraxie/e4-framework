package de.scandio.e4.testpackages

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.util.WorkerUtils
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getILoggerFactory


abstract class TestPackageTestRun {

    val loggerContext = getILoggerFactory() as LoggerContext

    private val log = LoggerFactory.getLogger(javaClass)

    protected var webConfluence: WebConfluence? = null
    protected var restConfluence: RestConfluence? = null


    abstract fun getBaseUrl(): String
    abstract fun getOutDir(): String
    abstract fun getUsername(): String
    abstract fun getPassword(): String
    abstract fun getTestPackage(): TestPackage

    init {
        setLogLevel("org.apache", Level.ERROR)
        setLogLevel("org.openqa.selenium.phantomjs.PhantomJSDriverService", Level.ERROR)
    }

    protected fun setLogLevel(packagePath: String, level: Level) {
        loggerContext.getLogger(packagePath).level = level
    }

    protected fun setup() {

        this.webConfluence = WorkerUtils.newChromeWebClient(
                getBaseUrl(), getOutDir(), getUsername(), getPassword()) as WebConfluence
        this.restConfluence = RestConfluence(getBaseUrl(), getUsername(), getPassword())
    }

    protected fun shutdown() {
        webConfluence!!.driver.quit()
    }

    protected fun executeTestPackage(testPackage: TestPackage) {
        log.info("==============================================================")
        log.info("START executing ${testPackage.virtualUsers.size} virtual users")
        for (virtualUserClass in testPackage.virtualUsers) {
            val virtualUser = virtualUserClass.newInstance()
            log.info("Executing virtual user ${virtualUser.javaClass.simpleName}")
            val measurement = executeActions(virtualUser.getActions(this.webConfluence, this.restConfluence))
            log.info("[MEASURE] Total time taken for VirtualUser ${virtualUser.javaClass.simpleName}: ${measurement.totalTimeTaken} (Total actions run: ${measurement.numActionsRun} - Actions excluded from measurement: ${measurement.numExcludedActions})")
        }
        log.info("DONE executing ${testPackage.virtualUsers.size} virtual users")
        log.info("==============================================================")
    }

    protected fun executeTestPackagePrepare(testPackage: TestPackage) {
        log.info("==============================================================")
        log.info("START executing ${testPackage.setupActions.size} setup actions")
        executeActions(testPackage.setupActions)
        log.info("DONE executing setup actions")
        log.info("==============================================================")
    }

    protected fun executeAction(action: Action) {
        action.execute(webConfluence!!, restConfluence!!)
        val runtimeName = "afteraction-${action.javaClass.simpleName}"
        webConfluence!!.takeScreenshot(runtimeName)
        webConfluence!!.dumpHtml(runtimeName)
        log.info("Time taken: ${action.timeTaken}")
    }

    protected fun executeActions(actions: ActionCollection): Measurement {
        var totalTimeTaken: Long = 0
        var numExcludedActions = 0
        var numActionsRun = 0
        for (action in actions) {
            try {
                action.execute(webConfluence!!, restConfluence!!)
                if (!actions.isExcludedFromMeasurement(action)) {
                    totalTimeTaken += action.timeTaken
                    numActionsRun += 1
                    log.info("Time taken for action ${action.javaClass.simpleName}: ${action.timeTaken}")
                } else {
                    numExcludedActions += 1
                }
            } finally {
                val runtimeName = "afteraction-${action.javaClass.simpleName}"
                webConfluence!!.takeScreenshot(runtimeName)
                webConfluence!!.dumpHtml(runtimeName)
            }
        }
        return Measurement(totalTimeTaken, numExcludedActions, numActionsRun)
    }

    class Measurement(val totalTimeTaken: Long, val numExcludedActions: Int, val numActionsRun: Int)

}