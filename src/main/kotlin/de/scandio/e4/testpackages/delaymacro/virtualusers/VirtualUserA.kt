package de.scandio.e4.testpackages.delaymacro.virtualusers

import de.scandio.e4.testpackages.delaymacro.actions.ViewDelayPageAction
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.VirtualUser

class VirtualUserA : VirtualUser {

    override fun getActions(): List<Action> {
        val list = arrayListOf<Action>()
        list.add(ViewDelayPageAction())
        return list
    }
}