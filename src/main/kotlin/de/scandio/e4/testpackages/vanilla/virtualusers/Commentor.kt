package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.scenarios.ViewPageScenario
import de.scandio.e4.worker.collections.ScenarioCollection
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * Confluence Commentor Scenario.
 *
 * Assumptions:
 *
 * Actions:
 *
 * @author Felix Grund
 */
class Commentor : VirtualUser {

    override fun getScenarios(): MutableList<Scenario> {
        val scenarios = ScenarioCollection()
        val spaceKey = "E4"
        scenarios.add(ViewPageScenario(spaceKey, "E4 Reader Page 1"))
//        list.add(ViewPageScenario(spaceKey, "E4 Reader Page 2"))
//        list.add(ViewPageScenario(spaceKey, "E4 Reader Page 3"))
//        list.add(ViewBlogpostScenario(spaceKey, "E4 Reader Blogpost 1","2019/05/21"))
//        list.add(ViewBlogpostScenario(spaceKey, "E4 Reader Blogpost 2","2019/05/21"))
//        list.add(CheckPageRestrictionsScenario(spaceKey, "E4 Reader Page 1"))
//        list.add(ViewPageInfoScenario(spaceKey, "E4 Reader Page 1"))
        return scenarios
    }
}