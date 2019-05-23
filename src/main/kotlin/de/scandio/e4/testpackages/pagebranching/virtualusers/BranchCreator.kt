package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser
import java.util.*


/**
 * PageBranching BranchCreator VirtualUser.
 *
 * Assumptions:
 * - Space with key "PB"
 * - 3 pages in space "PB" with titles "BranchCreator Origin 1", "BranchCreator Origin 2",
 *   "BranchCreator Origin 3"
 *
 * Actions (all SELENIUM):
 * - Create branch of page "BranchCreator Origin 1" with title "BranchCreator Origin 1-1 (START_TIME)"
 * - Create another branch of page "BranchCreator Origin 1" with title "BranchCreator Origin 1-2 (START_TIME)"
 * - Create branch of page "BranchCreator Origin 2" with title "BranchCreator Origin 2-1 (START_TIME)"
 * - Create another branch of page "BranchCreator Origin 2" with title "BranchCreator Origin 2-2 (START_TIME)"
 * - Create branch of page "BranchCreator Origin 3" with title "BranchCreator Origin 3-1 (START_TIME)"
 * - Create another branch of page "BranchCreator Origin 3" with title "BranchCreator Origin 3-2 (START_TIME)"
 *
 * @author Felix Grund
 */
open class BranchCreator : VirtualUser {

    protected var virtualUserStartTime: Long = 0

    protected var start: Long = 0
    protected var end: Long = 0

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        virtualUserStartTime = Date().time
        return actions
    }
}