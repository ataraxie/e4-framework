package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.worker.collections.ActionCollection


/**
 * PageBranching BranchMerger VirtualUser. Extends BranchCreator and creates
 * all branches that it will later merge first.
 *
 * Assumptions:
 * - Space with spacekey "PB"
 * - Page with title "BranchOverviewReader Origin" in space "PB"
 * -
 *
 * Actions (all SELENIUM):
 * - ALL ACTIONS FROM BranchCreator
 * - Create page with title "Page Branching Overview (START_TIME)" in space "PB"
 * - Insert pagebranching-overview macro into the content of the created page
 *
 * @author Felix Grund
 */
class BranchOverviewReader : BranchCreator() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.addAll(super.getActions())
        return actions
    }

}