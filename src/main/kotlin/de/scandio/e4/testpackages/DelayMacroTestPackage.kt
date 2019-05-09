package de.scandio.e4.testpackages

import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.interfaces.VirtualUser

class DelayMacroTestPackage : TestPackage {

    override fun getSetupScenarios(): List<Class<Scenario>> {
        val list = arrayListOf<Class<Scenario>>()
        return list
    }

    override fun getVirtualUsers(): List<Class<VirtualUser>> {
        val list = arrayListOf<Class<VirtualUser>>()
        return list
    }

}