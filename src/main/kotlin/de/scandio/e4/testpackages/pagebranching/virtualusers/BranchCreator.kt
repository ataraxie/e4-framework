package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.pagebranching.actions.CreateBranchAction
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.ViewPageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient
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
        actions.add(CreateBranchAction("PB"))
        return actions
    }

}