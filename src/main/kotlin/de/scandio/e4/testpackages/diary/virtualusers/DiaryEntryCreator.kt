package de.scandio.e4.testpackages.gitsnippets.virtualusers

import de.scandio.e4.testpackages.diary.actions.CreateDiaryEntryAction
import de.scandio.e4.testpackages.livelyblogs.actions.CreateRandomLivelyBlogMacroPage
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser

class DiaryEntryCreator : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(CreateDiaryEntryAction("DR", "macros"))
        return actions
    }

}
