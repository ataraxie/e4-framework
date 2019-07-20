package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewPageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * === BranchedPageReader ===
 *
 * PageBranching BranchedPageReader VirtualUser.
 *
 * Assumptions:
 * - Space with spacekey "PB"
 * - Page with title "PB Root Origin" in space "PB"
 * - Branched page of page "PB Root Origin"
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - View the original page from which the branch was created
 *
 * @author Felix Grund
 */
class BranchedPageReader : VirtualUser() {

    override fun onInit(restClient: RestClient) {
    }

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewPageAction("PB", "Branch 1: PB Root Origin"))
        return actions
    }

}