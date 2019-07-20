package de.scandio.e4.testpackages.pagebranching

import de.scandio.e4.testpackages.pagebranching.actions.CreateBranchAction
import de.scandio.e4.testpackages.pagebranching.actions.CreateOverviewPageAction
import de.scandio.e4.testpackages.pagebranching.virtualusers.*
import de.scandio.e4.testpackages.vanilla.actions.AddSpaceGroupPermissionAction
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.CreateSpaceAction
import de.scandio.e4.testpackages.vanilla.virtualusers.*
import de.scandio.e4.worker.client.ApplicationName
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
 * - Create branch of page "PB Root Origin"
 * - Create page with title "PB BranchOverviewReader Origin"
 * - Create branch of page "PB BranchOverviewReader Origin"
 * - Edit page "PB BranchOverviewReader Origin" and add "pagebranching-overview-macro" into the page content
 *
 * Virtual Users:
 * - BranchCreator (weight 0.04): creates page branches
 * - BranchMerger (weight 0.04): merges page branches
 * - BranchOverviewCreator (weight 0.04): creates page branching overview pages
 * - BranchOverviewReader (weight 0.04): reads page branching overview pages
 * - BranchedPageReader (weight 0.16): reads page branches (branches from an origin page)
 * - OriginPageReader (weight 0.16): reads origin pages (from which branches were created)
 *
 * Sum of weight is 0.48 which leaves 0.52 for vanilla virtual users.
 *
 * @author Felix Grund
 */
class PageBranchingTestPackage: TestPackage {

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()

        virtualUsers.add(Commentor::class.java, 0.04)
        virtualUsers.add(Reader::class.java, 0.2)
        virtualUsers.add(Creator::class.java, 0.04)
        virtualUsers.add(Searcher::class.java, 0.08)
        virtualUsers.add(Editor::class.java, 0.08)
        virtualUsers.add(Dashboarder::class.java, 0.08)

        virtualUsers.add(BranchCreator::class.java, 0.04)
        virtualUsers.add(BranchMerger::class.java, 0.04)
        virtualUsers.add(BranchOverviewCreator::class.java, 0.04)
        virtualUsers.add(BranchOverviewReader::class.java, 0.04)
        virtualUsers.add(BranchedPageReader::class.java, 0.16)
        virtualUsers.add(OriginPageReader::class.java, 0.16)
        return virtualUsers
    }

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        val spaceKey = "PB"
        val spaceName = "E4 Page Branching Space"
        var originPageTitle = "PB Root Origin"

        // Assumption for all virtual users
        actions.add(CreateSpaceAction(spaceKey, spaceName))

        actions.add(AddSpaceGroupPermissionAction("PB", "confluence-users", "removepage", true))

        // Assumption for BranchedPageReader and OriginPageReader
        actions.add(CreatePageAction(spaceKey, originPageTitle))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 1"))

        // Assumption for BranchOverviewReader
        originPageTitle = "PB BranchOverviewReader Origin"
        actions.add(CreatePageAction(spaceKey, originPageTitle))
        actions.add(CreateBranchAction(spaceKey, originPageTitle, "Branch 1"))

        actions.add(CreateOverviewPageAction(spaceKey, originPageTitle))

        return actions
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }


}