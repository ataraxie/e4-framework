package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewPageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * === BranchOverviewReader ===
 *
 * PageBranching BranchOverviewReader VirtualUser.
 *
 * Assumptions:
 * - Space with spacekey "PB"
 * - Page with title "PB BranchOverviewReader Origin" in space "PB" containing a pagebranching-overview-macro
 * - Page branches of "PB BranchOverviewReader Origin" page
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - View page with title "PB BranchOverviewReader Origin" in space "PB"
 *
 * @author Felix Grund
 */
class BranchOverviewReader : VirtualUser() {
    
    override fun onInit(restClient: RestClient) {
    }

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewPageAction("PB", "PB BranchOverviewReader Origin"))
        return actions
    }
}