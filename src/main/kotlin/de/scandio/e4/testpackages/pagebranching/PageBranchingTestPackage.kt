package de.scandio.e4.testpackages.pagebranching

import de.scandio.e4.testpackages.pagebranching.virtualusers.*
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
        virtualUsers.add(BranchCreator(), 0.05)
        virtualUsers.add(BranchMerger(), 0.05)
//        virtualUsers.add(BranchOverviewCreator(), 0.05)
//        virtualUsers.add(BranchOverviewReader(), 0.1)
//        virtualUsers.add(BranchedPageReader(), 0.25)
//        virtualUsers.add(OriginPageReader(), 0.5)
        return virtualUsers
    }

}