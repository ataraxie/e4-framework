package de.scandio.e4.testpackages.delaymacro

import de.scandio.e4.scenarios.RestCreatePageScenario
import de.scandio.e4.scenarios.RestCreateSpaceScenario
import de.scandio.e4.testpackages.delaymacro.virtualusers.VirtualUserA
import de.scandio.e4.worker.collections.ScenarioCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.interfaces.VirtualUser
import java.util.*

class DelayMacroTestPackage : TestPackage {

    override fun getSetupScenarios(): ScenarioCollection {
        val list = ScenarioCollection()
        val time = Date().time
        list.add(RestCreateSpaceScenario("E4", "E4 Test Space"))
        val pageContent = "<p>Hallo</p>"
//        val pageContent = "<p><ac:structured-macro ac:name=\"delay\" ac:schema-version=\"1\" ac:macro-id=\"0a2cc056-7588-4cac-bb74-e76e61efbf94\"><ac:parameter ac:name=\"seconds\">1</ac:parameter></ac:structured-macro></p>"
        list.add(RestCreatePageScenario("E4 Test Page ($time)", "E4", pageContent, "8159238"))
        return list
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val list = VirtualUserCollection()
        list.add(VirtualUserA(), 1.0)
        return list
    }

}