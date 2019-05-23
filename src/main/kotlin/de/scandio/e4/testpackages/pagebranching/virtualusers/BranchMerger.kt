package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.worker.collections.ActionCollection


/**
 * PageBranching BranchMerger VirtualUser. Extends BranchCreator and creates
 * all branches that it will later merge first.
 *
 * Assumptions:
 * - ALL ASSUMPTIONS FROM BranchCreator
 *
 * Actions (all SELENIUM):
 * - ALL ACTIONS FROM BranchCreator
 * - Merge branch "BranchCreator Origin 1-1 (START_TIME)" into "BranchCreator Origin 1"
 * - Merge branch "BranchCreator Origin 1-2 (START_TIME)" into "BranchCreator Origin 1"
 * - Merge branch "BranchCreator Origin 2-1 (START_TIME)" into "BranchCreator Origin 2"
 * - Merge branch "BranchCreator Origin 2-1 (START_TIME)" into "BranchCreator Origin 2"
 * - Merge branch "BranchCreator Origin 3-1 (START_TIME)" into "BranchCreator Origin 3"
 * - Merge branch "BranchCreator Origin 3-2 (START_TIME)" into "BranchCreator Origin 3"
 *
 * @author Felix Grund
 */
class BranchMerger : BranchCreator() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.addAll(super.getActions())
        return actions
    }
}