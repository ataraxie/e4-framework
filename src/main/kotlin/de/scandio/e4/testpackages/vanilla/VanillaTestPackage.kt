package de.scandio.e4.testpackages.vanilla

import de.scandio.e4.testpackages.vanilla.virtualusers.*
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.collections.VirtualUserCollection

class VanillaTestPackage: TestPackage {

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        return actions
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
        virtualUsers.add(Commentor::class.java, 0.08)
        virtualUsers.add(Reader::class.java, 0.36)
        virtualUsers.add(Creator::class.java, 0.08)
        virtualUsers.add(Searcher::class.java, 0.16)
        virtualUsers.add(Editor::class.java, 0.16)
        virtualUsers.add(Dashboarder::class.java, 0.16)
        return virtualUsers
    }

}