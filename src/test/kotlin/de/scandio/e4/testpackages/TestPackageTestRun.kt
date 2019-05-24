package de.scandio.e4.testpackages

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.testpackages.pagebranching.actions.CreateBranchAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.util.WorkerUtils
import org.junit.Test
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getILoggerFactory
import java.util.*


abstract class TestPackageTestRun {

    protected var webConfluence: WebConfluence? = null
    protected var restConfluence: RestConfluence? = null

    abstract fun getBaseUrl(): String
    abstract fun getOutDir(): String
    abstract fun getUsername(): String
    abstract fun getPassword(): String
    abstract fun getTestPackage(): TestPackage

    fun setup() {
        val loggerContext = getILoggerFactory() as LoggerContext
        loggerContext.getLogger("org.apache").setLevel(Level.ERROR)
        loggerContext.getLogger("org.openqa.selenium.phantomjs.PhantomJSDriverService").setLevel(Level.ERROR)

        this.webConfluence = WorkerUtils.newChromeWebClient(
                getBaseUrl(), getOutDir(), getUsername(), getPassword()) as WebConfluence
        this.restConfluence = RestConfluence(getBaseUrl(), getUsername(), getPassword())
    }

    fun execute() {
        try {
            val actions = ActionCollection()
            actions.add(CreateBranchAction("PB", "BranchCreator Origin Manual", "Branch 1"))
            val measurement = executeActions(actions)
            print("Time taken: ${measurement.totalTimeTaken}\n")
        } finally {
            webConfluence!!.driver.quit()
        }
    }

    fun executeTestPackage(testPackage: TestPackage) {
        for (virtualUser in testPackage.virtualUsers) {
            val measurement = executeActions(virtualUser.actions)
            print("Total time taken for VirtualUser ${virtualUser.javaClass.simpleName}: ${measurement.totalTimeTaken} (${measurement.numExcludedActions} actions excluded from measurement)\n")
        }
    }

    fun executeAction(action: Action) {
        action.execute(webConfluence!!, restConfluence!!)
        val runtimeName = "afteraction-${action.javaClass.simpleName}-${Date().time}"
        webConfluence!!.takeScreenshot(runtimeName)
        webConfluence!!.dumpHtml(runtimeName)
        print("Time taken: ${action.timeTaken}\n")
    }

    fun executeActions(actions: ActionCollection): Measurement {
        var totalTimeTaken: Long = 0
        var numExcludedActions = 0
        var numActionsRun = 0
        for (action in actions) {
            try {
                action.execute(webConfluence!!, restConfluence!!)
                if (!actions.isExcludedFromMeasurement(action)) {
                    totalTimeTaken += action.timeTaken
                    numActionsRun += 1
                    print("Time taken for action ${action.javaClass.simpleName}: ${action.timeTaken}\n")
                } else {
                    numExcludedActions += 1
                }
            } finally {
                val runtimeName = "afteraction-${action.javaClass.simpleName}-${Date().time}"
                webConfluence!!.takeScreenshot(runtimeName)
                webConfluence!!.dumpHtml(runtimeName)
            }
        }
        return Measurement(totalTimeTaken, numExcludedActions, numActionsRun)
    }

    class Measurement(val totalTimeTaken: Long, val numExcludedActions: Int, val numActionsRun: Int)

}