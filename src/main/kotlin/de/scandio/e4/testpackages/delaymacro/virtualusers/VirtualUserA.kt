package de.scandio.e4.testpackages.delaymacro.virtualusers

import de.scandio.e4.testpackages.delaymacro.scenarios.ViewDelayPageScenario
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.VirtualUser

class VirtualUserA : VirtualUser {
    override fun getUsername(): String {
        return "admin"
    }

    override fun getPassword(): String {
        return "admin"
    }

    override fun getScenarios(): List<Scenario> {
        val list = arrayListOf<Scenario>()
        list.add(ViewDelayPageScenario(username, password))
        return list
    }
}