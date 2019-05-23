package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewDashboardAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * Confluence Commentor VirtualUser.
 *
 * Assumptions:
 * - No specific assumptions (only need a dashboard)
 *
 * Actions:
 * - View the Confluence dashboard (SELENIUM)
 *
 * @author Felix Grund
 */
class Dashboarder : VirtualUser {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewDashboardAction())
        return actions
    }
}