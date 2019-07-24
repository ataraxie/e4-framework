package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.pagebranching.actions.CreateBranchAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * === BranchCreator ===
 *
 * PageBranching BranchCreator VirtualUser.
 *
 * Assumptions:
 * - Space with key "PB"
 *
 * Preparation:
 * - Creates a random Confluence page
 *
 * Actions:
 * - Creates a branch page of previously created page
 *
 * @author Felix Grund
 */
open class BranchCreator : VirtualUser() {

    protected var virtualUserStartTime: Long = 0

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(CreateBranchAction("PB"))
        return actions
    }

}