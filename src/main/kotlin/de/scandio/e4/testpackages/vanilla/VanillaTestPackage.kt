package de.scandio.e4.testpackages.vanilla

import de.scandio.e4.testpackages.vanilla.virtualusers.Reader
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.interfaces.VirtualUser

class VanillaTestPackage: TestPackage {

    override fun getSetupScenarios(): List<Scenario> {
        val list = arrayListOf<Scenario>()
        return list
    }

    override fun getVirtualUsers(): List<VirtualUser> {
        val list = arrayListOf<VirtualUser>()
        list.add(Reader())
        return list
    }

}