package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.livelyblogs.actions.ViewRandomBlogpostOverview
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser

class LivelyBlogNavigator : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewRandomBlogpostOverview())
        return actions
    }

}
