package de.scandio.e4.testpackages.teamadmin

import de.scandio.e4.testpackages.gitsnippets.virtualusers.SpaceGroupCreator
import de.scandio.e4.testpackages.vanilla.virtualusers.*
import de.scandio.e4.worker.client.ApplicationName
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.TestPackage

/**
 * === TeamAdminTestPackage ===
 *
 * Test package for app "Team Admin for Confluence".
 *
 * Assumptions:
 * - Running Confluence
 * - App Team Admin installed
 * - A space with key "TEST"
 * - confluence-users group as ADMINISTERSPACE permission to be able to test space permissions in a randomized fashion
 *
 * Setup:
 * - No automated setup actions
 *
 * Virtual Users:
 * - SpaceGroupCreator: creates a space admin group with a randomized set of permissions
 *
 * Weight is 0.1 which leaves 0.9 for vanilla virtual users.
 *
 * @author Felix Grund
 */
class TeamAdminTestPackage: TestPackage {

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        return actions
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
        // 0.9
        virtualUsers.add(Commentor::class.java, 0.08)
        virtualUsers.add(Reader::class.java, 0.3)
        virtualUsers.add(Creator::class.java, 0.1)
        virtualUsers.add(Searcher::class.java, 0.16)
        virtualUsers.add(Editor::class.java, 0.1)
        virtualUsers.add(Dashboarder::class.java, 0.16)

        // 0.1
        virtualUsers.add(SpaceGroupCreator::class.java, 0.1)
        return virtualUsers
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }

}