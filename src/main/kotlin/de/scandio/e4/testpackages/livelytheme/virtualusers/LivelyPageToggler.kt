package de.scandio.e4.testpackages.livelytheme.virtualusers

import de.scandio.e4.testpackages.livelytheme.actions.FavPageToggleAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * === LivelyPageToggler ===
 *
 * LivelyTheme LivelyPageToggler VirtualUser.
 *
 * Assumptions:
 * - NONE
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - Navigate to a random page and toggle the favorite page button
 *
 * @author Felix Grund
 */
open class LivelyPageToggler : VirtualUser() {


    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(FavPageToggleAction())
        return actions
    }

}