package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.scenarios.QuicksearchScenario
import de.scandio.e4.testpackages.vanilla.scenarios.ViewPageScenario
import de.scandio.e4.worker.collections.ScenarioCollection
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * Confluence Searcher Scenario.
 *
 * Assumptions:
 *
 * Actions:
 *
 * @author Felix Grund
 */
class Searcher : VirtualUser {

    override fun getScenarios(): MutableList<Scenario> {
        val scenarios = ScenarioCollection()
        scenarios.add(QuicksearchScenario("E4"))
        scenarios.add(QuicksearchScenario("E4 Reader"))
        scenarios.add(QuicksearchScenario("E4 Reader Page 1"))
        return scenarios
    }
}