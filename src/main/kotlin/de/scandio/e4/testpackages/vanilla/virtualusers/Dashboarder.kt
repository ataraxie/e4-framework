package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewDashboardAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * Confluence Dashboarder VirtualUser.
 *
 * Actions:
 * - Views the Confluence dashboard
 *
 * @author Felix Grund
 */
class Dashboarder : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewDashboardAction())
        return actions
    }
}