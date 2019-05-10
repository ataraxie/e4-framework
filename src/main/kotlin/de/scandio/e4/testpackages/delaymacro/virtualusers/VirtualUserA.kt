package de.scandio.e4.testpackages.delaymacro.virtualusers

import de.scandio.e4.testpackages.delaymacro.scenarios.ViewDelayPageScenario
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.VirtualUser

class VirtualUserA : VirtualUser {
    override fun getScenarios(): List<Scenario> {
        val list = arrayListOf<Scenario>()
        // TODO: credentials!
        list.add(ViewDelayPageScenario("admin", "admin"))
        return list
    }
}