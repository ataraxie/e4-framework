package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewPageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * === OriginPageReader ===
 *
 * PageBranching OriginPageReader VirtualUser.
 *
 * Assumptions:
 * - Space with spacekey "PB"
 * - Page with title "PB Root Origin" in space "PB"
 * - Branched page of "PB Root Origin"
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - View page "PB Root Origin" in space "PB"
 *
 * @author Felix Grund
 */
open class OriginPageReader : VirtualUser() {

    override fun onInit(restClient: RestClient) {
    }

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewPageAction("PB", "PB Root Origin"))
        return actions
    }

}