package de.scandio.e4.testpackages.gitsnippets.virtualusers

import de.scandio.e4.testpackages.diary.actions.AddSpaceGroupAction
import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser

class SpaceGroupCreator : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(AddSpaceGroupAction())
        return actions
    }

}
