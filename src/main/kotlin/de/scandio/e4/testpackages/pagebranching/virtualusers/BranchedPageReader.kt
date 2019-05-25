package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewPageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * === BranchedPageReader ===
 *
 * PageBranching BranchedPageReader VirtualUser.
 *
 * Assumptions:
 * - Space with spacekey "PB"
 * - Page with title "PB Root Origin" in space "PB"
 * - 5 branches of page "PB Root Origin" with branch names "Branch X", where X is the index
 *
 * Preparation:
 * - NONE
 *
 * Actions (all SELENIUM):
 * - View each of the 5 branches of page with title "PageReader Origin" in space "PB"
 *
 * @author Felix Grund
 */
class BranchedPageReader : VirtualUser {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewPageAction("PB", "Branch 1: PB Root Origin"))
        actions.add(ViewPageAction("PB", "Branch 2: PB Root Origin"))
        actions.add(ViewPageAction("PB", "Branch 3: PB Root Origin"))
        actions.add(ViewPageAction("PB", "Branch 4: PB Root Origin"))
        actions.add(ViewPageAction("PB", "Branch 5: PB Root Origin"))
        return actions
    }

}