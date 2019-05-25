package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.pagebranching.actions.CreateBranchAction
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.ViewPageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser
import java.util.*


/**
 * === BranchCreator ===
 *
 * PageBranching BranchCreator VirtualUser.
 *
 * Assumptions:
 * - Space with key "PB"
 *
 * Preparation:
 * - Create 3 pages in space "PB" with titles
 *   - "PB Origin 1 (TIMESTAMP)"
 *   - "PB Origin 2 (TIMESTAMP)"
 *   - "PB Origin 3 (TIMESTAMP)"
 *
 * Actions (all SELENIUM):
 * - Create 2 branches of each page with branch name "Branch X", where X is the index of creation
 *
 * @author Felix Grund
 */
open class BranchCreator : VirtualUser {

    protected var virtualUserStartTime: Long = 0

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        virtualUserStartTime = Date().time

        // PREPARATION
        actions.addExcludeFromMeasurement(CreatePageAction("PB", "PB Origin 1 ($virtualUserStartTime)"))
        actions.addExcludeFromMeasurement(CreatePageAction("PB", "PB Origin 2 ($virtualUserStartTime)"))
        actions.addExcludeFromMeasurement(CreatePageAction("PB", "PB Origin 3 ($virtualUserStartTime)"))

        // ACTIONS
        actions.add(CreateBranchAction("PB", "PB Origin 1 ($virtualUserStartTime)", "Branch 1"))
        actions.add(CreateBranchAction("PB", "PB Origin 1 ($virtualUserStartTime)", "Branch 2"))
        actions.add(CreateBranchAction("PB", "PB Origin 2 ($virtualUserStartTime)", "Branch 1"))
        actions.add(CreateBranchAction("PB", "PB Origin 2 ($virtualUserStartTime)", "Branch 2"))
        actions.add(CreateBranchAction("PB", "PB Origin 3 ($virtualUserStartTime)", "Branch 1"))
        actions.add(CreateBranchAction("PB", "PB Origin 3 ($virtualUserStartTime)", "Branch 2"))

        return actions
    }

}