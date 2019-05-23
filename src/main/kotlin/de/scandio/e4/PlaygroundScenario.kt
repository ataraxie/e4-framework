package de.scandio.e4

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.testpackages.vanilla.actions.SearchAndClickFiltersAction
import de.scandio.e4.testpackages.vanilla.virtualusers.Searcher
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.confluence.rest.RestConfluence

class PlaygroundScenario(
        val webConfluence: WebConfluence,
        val restConfluence: RestConfluence
) {

    fun execute() {
        val actions = ActionCollection()
        actions.add(SearchAndClickFiltersAction("E4"))
//        actions.add(QuicksearchAction("E4"))
//        actions.add(QuicksearchAction("E4 Reader"))
//        actions.add(QuicksearchAction("E4 Reader Page 1"))
//        executeScenarios(actions)

        executeScenarios(Searcher().actions)
    }

    fun executeScenarios(actions: ActionCollection) {
        var totalTimeTaken: Long = 0
        for (action in actions) {
            try {
                action.execute(webConfluence, restConfluence)
                totalTimeTaken += action.timeTaken
            } finally {
                val runtimeName = "afteraction-${action.javaClass.simpleName}"
                webConfluence.takeScreenshot(runtimeName)
                webConfluence.dumpHtml(runtimeName)
                webConfluence.driver.quit()
            }

        }
        print("Total time taken: $totalTimeTaken")
    }

}