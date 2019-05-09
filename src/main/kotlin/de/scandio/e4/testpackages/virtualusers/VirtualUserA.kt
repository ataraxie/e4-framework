package de.scandio.e4.testpackages.virtualusers

import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.VirtualUser

class VirtualUserA : VirtualUser {
    override fun getScenarios(): List<Class<Scenario>> {
        val list = arrayListOf<Class<Scenario>>()
        return list
    }
}