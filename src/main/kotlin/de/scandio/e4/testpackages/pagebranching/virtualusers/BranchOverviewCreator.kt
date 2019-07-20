package de.scandio.e4.testpackages.pagebranching.virtualusers

import de.scandio.e4.testpackages.pagebranching.actions.CreateBranchAction
import de.scandio.e4.testpackages.pagebranching.actions.CreateOverviewPageAction
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import java.util.*


/**
 * === BranchOverviewCreator ===
 *
 * PageBranching BranchOverviewCreator VirtualUser.
 *
 * Assumptions:
 * - Space with spacekey "PB"
 *
 * Preparation:
 * - Create page in space "PB" with title "PB BranchOverviewCreator Origin (START_TIME)"
 * - Create 5 branches of page with the original page title and " - Branch X" appended, where X is the number
 *
 * Actions:
 * - Edit the page "PB BranchOverviewCreator Origin (START_TIME)" and add the pagebranching-overview macro
 *
 * @author Felix Grund
 */
class BranchOverviewCreator : VirtualUser() {

    override fun onInit(restClient: RestClient) {
    }

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        val virtualUserStartTime = Date().time
        val spaceKey = "PB"
        val pageTitle = "PB BranchOverviewCreator Origin ($virtualUserStartTime)"

        // PREPARATION

        actions.addExcludeFromMeasurement(CreatePageAction(spaceKey, pageTitle))

        actions.addExcludeFromMeasurement(CreateBranchAction(spaceKey, pageTitle, "Branch 1"))
        actions.addExcludeFromMeasurement(CreateBranchAction(spaceKey, pageTitle, "Branch 2"))
        actions.addExcludeFromMeasurement(CreateBranchAction(spaceKey, pageTitle, "Branch 3"))
        actions.addExcludeFromMeasurement(CreateBranchAction(spaceKey, pageTitle, "Branch 4"))
        actions.addExcludeFromMeasurement(CreateBranchAction(spaceKey, pageTitle, "Branch 5"))

        actions.add(CreateOverviewPageAction(spaceKey, pageTitle))

        return actions
    }

}