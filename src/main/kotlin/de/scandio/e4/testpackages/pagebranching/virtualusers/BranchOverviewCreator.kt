package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.worker.collections.ActionCollection


/**
 * PageBranching BranchMerger VirtualUser. Extends BranchCreator and creates
 * all branches that it will later merge first.
 *
 * Assumptions:
 * - Space with spacekey "PB"
 *
 * Actions (all SELENIUM):
 * - Create page in space "PB" with title "BranchOverviewCreator Origin (START_TIME)"
 * - Create 5 branches of page with the original page title and " - Branch X" appended, where X is the number
 * - Edit the page "BranchOverviewCreator Origin (START_TIME)" and add the pagebranching-overview macro
 *
 * @author Felix Grund
 */
class BranchOverviewCreator : BranchCreator() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.addAll(super.getActions())
        return actions
    }

}