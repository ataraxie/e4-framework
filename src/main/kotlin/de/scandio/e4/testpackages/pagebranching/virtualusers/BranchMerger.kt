package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.pagebranching.actions.MergeBranchAction
import de.scandio.e4.worker.collections.ActionCollection


/**
 * PageBranching BranchMerger VirtualUser. Extends BranchCreator and creates
 * all branches that it will later merge first.
 *
 * Assumptions:
 * - Space with key "PB"
 *
 * Preparation:
 * - ALL PREPARATIONS AND ACTIONS FROM BranchCreator
 *
 * Actions (all SELENIUM):
 * - Merge all branches into their origin pages
 *
 * @author Felix Grund
 */
class BranchMerger : BranchCreator() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()

        // PREPARATION
        actions.addAllExcludeFromMeasurement(super.getActions())

        // ACTIONS
        actions.add(MergeBranchAction("PB", "Branch 1", "PB Origin 1 ($virtualUserStartTime)"))
        actions.add(MergeBranchAction("PB", "Branch 2", "PB Origin 1 ($virtualUserStartTime)"))
        actions.add(MergeBranchAction("PB", "Branch 1", "PB Origin 2 ($virtualUserStartTime)"))
        actions.add(MergeBranchAction("PB", "Branch 2", "PB Origin 2 ($virtualUserStartTime)"))
        actions.add(MergeBranchAction("PB", "Branch 1", "PB Origin 3 ($virtualUserStartTime)"))
        actions.add(MergeBranchAction("PB", "Branch 2", "PB Origin 3 ($virtualUserStartTime)"))

        return actions
    }
}