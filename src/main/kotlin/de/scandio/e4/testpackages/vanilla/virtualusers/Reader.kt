package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.scenarios.ViewPageScenario
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.VirtualUser

class Reader : VirtualUser {

    override fun getScenarios(): MutableList<Scenario> {
        val list = arrayListOf<Scenario>()
        val spaceKey = "E4"
        val pageTitle = "E4 Space Home"
        list.add(ViewPageScenario(spaceKey, pageTitle))
        return list
    }
}