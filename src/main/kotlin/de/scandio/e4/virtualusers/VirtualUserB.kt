package de.scandio.e4.virtualusers

import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.VirtualUser

class VirtualUserB : VirtualUser {
    override fun getScenarios(): List<Scenario> {
        val list = arrayListOf<Scenario>()
        return list
    }
}