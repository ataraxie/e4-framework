package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.livelyblogs.actions.CreateRandomLivelyBlogMacroPage
import de.scandio.e4.testpackages.livelytheme.actions.CreateRandomLivelyThemeMacroPage
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser

class LivelyBlogMacroPageCreator : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(CreateRandomLivelyBlogMacroPage("LB"))
        return actions
    }

}
