package de.scandio.e4.testpackages

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import de.scandio.e4.E4TestEnv
import de.scandio.e4.worker.client.NoopWebClient
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.Util
import org.openqa.selenium.Dimension
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getILoggerFactory


abstract class TestPackageTestRun {

    val loggerContext = getILoggerFactory() as LoggerContext

    private val log = LoggerFactory.getLogger(javaClass)

    protected var screenshotCount = 0
    protected val util: Util

    init {
        this.util = Util()
        setLogLevel("org.apache", Level.ERROR)
    }

    protected fun setLogLevel(packagePath: String, level: Level) {
        loggerContext.getLogger(packagePath).level = level
    }

    protected fun executeTestPackage(testPackage: TestPackage, howOften: Int = 1) {
        log.info("==============================================================")
        log.info("START executing ${testPackage.virtualUsers.size} virtual users")

        repeat(howOften) {
            for (virtualUserClass in testPackage.virtualUsers) {
                val virtualUser = virtualUserClass.newInstance()
                log.info("Executing virtual user ${virtualUser.javaClass.simpleName}")
                val measurement = executeActions(virtualUser.actions)
                log.info("[MEASURE] Total time taken for VirtualUser ${virtualUser.javaClass.simpleName}: ${measurement.totalTimeTaken} (Total actions run: ${measurement.numActionsRun} - Actions excluded from measurement: ${measurement.numExcludedActions})")
            }
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
        var webClient: WebClient = NoopWebClient()
        if (!action.isRestOnly()) {
            webClient = E4TestEnv.newAdminTestWebClient()
        }
        val restConfluence = E4TestEnv.newAdminTestRestClient()
        log.info("Executing action ${action.javaClass.simpleName}")
        val runtimeName = "afteraction-${action.javaClass.simpleName}"
        try {
            action.execute(webClient, restConfluence)
            log.info("Time taken: ${action.timeTaken}")
        } finally {
            if (!action.isRestOnly) {
                webClient.takeScreenshot(runtimeName)
                webClient.dumpHtml(runtimeName)
                webClient.quit()
            }
        }
    }

    protected fun executeActions(actions: ActionCollection): Measurement {
        var totalTimeTaken: Long = 0
        var numExcludedActions = 0
        var numActionsRun = 0
        for (action in actions) {
            var webClient: WebClient
            if (actions.allRestOnly()) {
                webClient = NoopWebClient()
            } else {
                webClient = E4TestEnv.newAdminTestWebClient()
            }
            val restClient = E4TestEnv.newAdminTestRestClient()
            try {
                log.info("Executing action ${action.javaClass.simpleName}")
                action.execute(webClient, restClient)
                if (!actions.isExcludedFromMeasurement(action)) {
                    totalTimeTaken += action.timeTaken
                    numActionsRun += 1
                    log.info("Time taken for action ${action.javaClass.simpleName}: ${action.timeTaken}")
                } else {
                    numExcludedActions += 1
                }
            } finally {
                val runtimeName = "afteraction-${action.javaClass.simpleName}"
                webClient.takeScreenshot(runtimeName)
                webClient.dumpHtml(runtimeName)
                webClient.quit()
            }
        }
        return Measurement(totalTimeTaken, numExcludedActions, numActionsRun)
    }

    open fun shot(webClient: WebClient) {
        this.screenshotCount += 1
        val path = this.util.takeScreenshot(webClient.webDriver, "${E4TestEnv.OUT_DIR}/$screenshotCount-test-run.png")
        print(path)
    }

    class Measurement(val totalTimeTaken: Long, val numExcludedActions: Int, val numActionsRun: Int)

}