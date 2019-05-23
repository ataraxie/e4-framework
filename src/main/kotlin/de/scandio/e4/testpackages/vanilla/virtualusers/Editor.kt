package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * Confluence Editor VirtualUser.
 *
 * Assumptions:
 * TODO: list assumptions
 *
 * Actions:
 * TODO: list actions
 *
 * @author Felix Grund
 */
class Editor : VirtualUser {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        TODO("Add Scenarios")
        return actions
    }
}