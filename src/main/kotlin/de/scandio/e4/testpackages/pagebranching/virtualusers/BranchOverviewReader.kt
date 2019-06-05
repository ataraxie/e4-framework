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
 * - At least 10 branches of page "PB BranchOverviewReader Origin" (so we have at least one full page in the overview)
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - View page with title "PB BranchOverviewReader Origin" in space "PB"
 *
 * @author Felix Grund
 */
class BranchOverviewReader : VirtualUser {
    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewPageAction("PB", "PB BranchOverviewReader Origin"))
        return actions
    }
}