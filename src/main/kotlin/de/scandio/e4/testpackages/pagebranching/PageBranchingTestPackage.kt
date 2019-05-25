package de.scandio.e4.testpackages.pagebranching

import de.scandio.e4.testpackages.pagebranching.actions.CreateBranchAction
import de.scandio.e4.testpackages.pagebranching.actions.CreateOverviewPageAction
import de.scandio.e4.testpackages.pagebranching.virtualusers.*
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.CreateSpaceAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.TestPackage

/**
 * === PageBranchingTestPackage ===
 *
 * Test package for app "Page Branching for Confluence".
 *
 * Assumptions:
 * - Running Confluence with Page Branching app installed
 *
 * Setup:
 * - Create space with key "PB" and name "E4 Page Branching Space"
 * - Create page with title "PB Root Origin" in space "PB"
 * - Create 5 branches of page "PB Root Origin" with name "Branch X", where X is the index of creation
 * - Create page with title "PB BranchOverviewReader Origin" in space "PB"
 * - Create 5 branches of page "PB BranchOverviewReader Origin" with name "Branch X", where X is the index of creation
 * - Edit page "PB BranchOverviewReader Origin" and add "pagebranching-overview-macro" into the page content
 *
 * Virtual Users:
 * - BranchCreator (weight 0.05): creates page branches
 * - BranchMerger (weight 0.05): merges page branches
 * - BranchOverviewCreator (weight 0.05): creates page branching overview pages
 * - BranchOverviewReader (weight 0.1): reads page branching overview pages
 * - BranchedPageReader (weight 0.25): reads page branches (branches from an origin page)
 * - OriginPageReader (weight 0.5): reads origin pages (from which branches were created)
 *
 * @author Felix Grund
 */
class PageBranchingTestPackage: TestPackage {

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
        virtualUsers.add(BranchCreator(), 0.05)
        virtualUsers.add(BranchMerger(), 0.05)
        virtualUsers.add(BranchOverviewCreator(), 0.05)
        virtualUsers.add(BranchOverviewReader(), 0.1)
        virtualUsers.add(BranchedPageReader(), 0.25)
        virtualUsers.add(OriginPageReader(), 0.5)
        return virtualUsers
    }

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        val spaceKey = "PB"
        val spaceName = "E4 Page Branching Space"
        var originPageTitle = "PB Root Origin"

        // Assumption for all virtual users
        actions.add(CreateSpaceAction(spaceKey, spaceName))

        // Assumption for BranchedPageReader and OriginPageReader
        actions.add(CreatePageAction(spaceKey, originPageTitle))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 1"))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 2"))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 3"))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 4"))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 5"))

        // Assumption for BranchOverviewReader
        originPageTitle = "PB BranchOverviewReader Origin"
        actions.add(CreatePageAction(spaceKey, originPageTitle))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 1"))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 2"))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 3"))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 4"))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 5"))
        actions.add(CreateOverviewPageAction(spaceKey, originPageTitle))

        return actions
    }

}