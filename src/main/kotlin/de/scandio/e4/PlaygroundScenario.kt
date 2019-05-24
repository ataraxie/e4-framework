package de.scandio.e4

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.testpackages.pagebranching.PageBranchingTestPackage
import de.scandio.e4.testpackages.vanilla.actions.SearchAndClickFiltersAction
import de.scandio.e4.testpackages.vanilla.virtualusers.Searcher
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.TestPackage

class PlaygroundScenario(
        val webConfluence: WebConfluence,
        val restConfluence: RestConfluence
) {

    fun execute() {
        val actions = ActionCollection()
//        actions.add(SearchAndClickFiltersAction("E4"))
//        actions.add(QuicksearchAction("E4"))
//        actions.add(QuicksearchAction("E4 Reader"))
//        actions.add(QuicksearchAction("E4 Reader Page 1"))
//        executeScenarios(actions)

        executeTestPackage(PageBranchingTestPackage())
        webConfluence.driver.quit()
    }

    fun executeTestPackage(testPackage: TestPackage) {
        for (virtualUser in testPackage.virtualUsers) {
            val measurement = executeActions(virtualUser.actions)
            print("Total time taken for VirtualUser ${virtualUser.javaClass.simpleName}: ${measurement.totalTimeTaken} (${measurement.numExcludedActions} actions excluded from measurement)")
        }
    }

    fun executeActions(actions: ActionCollection): Measurement {
        var totalTimeTaken: Long = 0
        var numExcludedActions = 0
        for (action in actions) {
            try {
                action.execute(webConfluence, restConfluence)
                if (!actions.isExcludedFromMeasurement(action)) {
                    totalTimeTaken += action.timeTaken
                } else {
                    numExcludedActions += 1
                }
            } finally {
                val runtimeName = "afteraction-${action.javaClass.simpleName}"
                webConfluence.takeScreenshot(runtimeName)
                webConfluence.dumpHtml(runtimeName)
            }
        }
        return Measurement(totalTimeTaken, numExcludedActions)
    }

    class Measurement(
            val totalTimeTaken: Long,
            val numExcludedActions: Int) {

    }

}