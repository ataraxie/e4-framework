package de.scandio.e4.testpackages.vanilla

import de.scandio.e4.testpackages.vanilla.virtualusers.Reader
import de.scandio.e4.testpackages.vanilla.virtualusers.Searcher
import de.scandio.e4.worker.collections.ScenarioCollection
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.collections.VirtualUserCollection

class VanillaTestPackage: TestPackage {

    override fun getSetupScenarios(): ScenarioCollection {
        val scenarios = ScenarioCollection()
        return scenarios
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
//        virtualUsers.add(Reader(), 0.7)
        virtualUsers.add(Searcher(), 0.1)
        return virtualUsers
    }

}