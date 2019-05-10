package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.scenarios.RestCreateSpaceScenario
import de.scandio.e4.testpackages.vanilla.scenarios.CreateSpaceScenario
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.VirtualUser

class VirtualCreator : VirtualUser {

    override fun getScenarios(): MutableList<Scenario> {
        val list = arrayListOf<Scenario>()
        list.add(CreateSpaceScenario("E4", "E4 Space"))
        return list
    }

}