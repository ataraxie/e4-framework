package de.scandio.e4

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.testpackages.vanilla.scenarios.QuicksearchScenario
import de.scandio.e4.testpackages.vanilla.scenarios.ViewPageScenario
import de.scandio.e4.worker.collections.ScenarioCollection
import de.scandio.e4.worker.confluence.rest.RestConfluence

class PlaygroundScenario(
        val webConfluence: WebConfluence,
        val restConfluence: RestConfluence
) {

    fun execute() {
        val scenarios = ScenarioCollection()
        scenarios.add(QuicksearchScenario("E4"))
        scenarios.add(QuicksearchScenario("E4 Reader"))
        scenarios.add(QuicksearchScenario("E4 Reader Page 1"))
        executeScenarios(scenarios)
    }

    fun executeScenarios(scenarios: ScenarioCollection) {
        var totalTimeTaken: Long = 0
        for (scenario in scenarios) {
            scenario.execute(webConfluence, restConfluence)
            totalTimeTaken += scenario.timeTaken
        }
        print("Total time taken: $totalTimeTaken")
    }

}