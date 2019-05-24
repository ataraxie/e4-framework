package de.scandio.e4.testpackages.delaymacro.virtualusers

import de.scandio.e4.testpackages.delaymacro.actions.ViewDelayPageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.VirtualUser

class VirtualUserA : VirtualUser {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewDelayPageAction())
        return actions
    }
}