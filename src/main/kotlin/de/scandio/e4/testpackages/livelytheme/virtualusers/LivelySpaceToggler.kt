package de.scandio.e4.testpackages.livelytheme.virtualusers

import de.scandio.e4.testpackages.livelytheme.actions.FavSpaceToggleAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * === LivelySpaceToggler ===
 *
 * LivelyTheme LivelySpaceToggler VirtualUser.
 *
 * Assumptions:
 * - NONE
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - Navigate to a random page and toggle the favorite space button
 *
 * @author Felix Grund
 */
open class LivelySpaceToggler : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(FavSpaceToggleAction())
        return actions
    }

}