package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser

class LivelyBlogMacroPageReader : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewRandomContent("LB", "macros", ".lively-blog-posts"))
        return actions
    }

}
