package de.scandio.e4.testpackages.delaymacro

import de.scandio.e4.testpackages.delaymacro.virtualusers.VirtualUserA
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.TestPackage

class DelayMacroTestPackage : TestPackage {

    override fun getSetupScenarios(): ActionCollection {
        val list = ActionCollection()
        return list
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val list = VirtualUserCollection()
        list.add(VirtualUserA(), 1.0)
        return list
    }

}