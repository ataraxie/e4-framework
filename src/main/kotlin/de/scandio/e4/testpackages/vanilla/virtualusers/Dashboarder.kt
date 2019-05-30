package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewDashboardAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


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

    override fun getActions(webClient: WebClient, restClient: RestClient): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewDashboardAction())
        return actions
    }
}