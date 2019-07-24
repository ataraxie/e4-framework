package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.livelyblogs.actions.SearchBlogpostOverview
import de.scandio.e4.testpackages.livelyblogs.actions.ViewRandomBlogpostOverview
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser

class LivelyBlogSearcher : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(SearchBlogpostOverview("LB", "Lively Blog"))
        return actions
    }

}
