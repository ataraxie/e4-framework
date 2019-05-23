package de.scandio.e4.testpackages.delaymacro.virtualusers

import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.VirtualUser

class VirtualUserB : VirtualUser {
    override fun getActions(): List<Action> {
        val list = arrayListOf<Action>()
        return list
    }
}