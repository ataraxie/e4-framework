package de.scandio.e4.testpackages.delaymacro

import de.scandio.e4.scenarios.CreatePageScenario
import de.scandio.e4.scenarios.CreateSpaceScenario
import de.scandio.e4.worker.interfaces.Scenario
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.interfaces.VirtualUser
import java.util.*

class DelayMacroTestPackage : TestPackage {

    override fun getSetupScenarios(): List<Scenario> {
        val list = arrayListOf<Scenario>()
        val time = Date().time
        list.add(CreateSpaceScenario("E4", "E4 Test Space"))
        val pageContent = "<p>Hallo</p>"
//        val pageContent = "<p><ac:structured-macro ac:name=\"delay\" ac:schema-version=\"1\" ac:macro-id=\"0a2cc056-7588-4cac-bb74-e76e61efbf94\"><ac:parameter ac:name=\"seconds\">1</ac:parameter></ac:structured-macro></p>"
        list.add(CreatePageScenario("E4 Test Page ($time)", "E4", pageContent, "8159238"))
        return list
    }

    override fun getVirtualUsers(): List<VirtualUser> {
        val list = arrayListOf<VirtualUser>()
        return list
    }

}