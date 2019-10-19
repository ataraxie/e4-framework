package de.scandio.e4.testpackages.diary

import de.scandio.e4.testpackages.gitsnippets.actions.SetupDiaryMacroPagesAction
import de.scandio.e4.testpackages.gitsnippets.virtualusers.DiaryEntryCreator
import de.scandio.e4.testpackages.gitsnippets.virtualusers.DiaryMacroPageReader
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.CreateSpaceAction
import de.scandio.e4.testpackages.vanilla.virtualusers.*
import de.scandio.e4.worker.client.ApplicationName
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.collections.VirtualUserCollection

class DiaryTestPackage: TestPackage {

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(CreateSpaceAction("DR", "Diary", true))
        actions.add(CreatePageAction("DR", "macros", "<ac:structured-macro ac:name=\"children\" />", true))
        actions.add(SetupDiaryMacroPagesAction("DR", "macros", 100))
        return actions
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
        // 0.88
        virtualUsers.add(Commentor::class.java, 0.08)
        virtualUsers.add(Reader::class.java, 0.32)
        virtualUsers.add(Creator::class.java, 0.1)
        virtualUsers.add(Searcher::class.java, 0.16)
        virtualUsers.add(Editor::class.java, 0.1)
        virtualUsers.add(Dashboarder::class.java, 0.12)

        // 0.12
        virtualUsers.add(DiaryEntryCreator::class.java, 0.04)
        virtualUsers.add(DiaryMacroPageReader::class.java, 0.08)
        return virtualUsers
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }

}