package de.scandio.e4.testpackages.pagebranching

import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.TestPackage

class PageBranchingTestPackage: TestPackage {

    override fun getSetupScenarios(): ActionCollection {
        val actions = ActionCollection()
        return actions
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
        return virtualUsers
    }

}