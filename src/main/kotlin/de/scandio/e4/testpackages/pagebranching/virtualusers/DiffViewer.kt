package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.pagebranching.actions.ViewDiffAction
import de.scandio.e4.worker.collections.ActionCollection


/**
 * === BranchMerger ===
 *
 * PageBranching BranchMerger VirtualUser. Extends BranchCreator and creates
 * all branches that it will later merge first.
 *
 * Assumptions:
 * - ALL ASSUMPTIONS FROM BranchCreator
 *
 * Preparation:
 * - ALL PREPARATIONS AND ACTIONS FROM BranchCreator
 *
 * Actions:
 * - Merge branch created by BranchCreator into the origin page
 *
 * @author Felix Grund
 */
class DiffViewer : BranchCreator() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewDiffAction("PB"))
        return actions
    }
}