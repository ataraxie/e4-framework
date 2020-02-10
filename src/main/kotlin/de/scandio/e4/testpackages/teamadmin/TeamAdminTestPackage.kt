package de.scandio.e4.testpackages.teamadmin

import de.scandio.e4.testpackages.gitsnippets.virtualusers.SpaceGroupCreator
import de.scandio.e4.testpackages.vanilla.virtualusers.*
import de.scandio.e4.worker.client.ApplicationName
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.TestPackage

/**
 * === DiaryTestPackage ===
 *
 * Test package for app "Diary for Confluence".
 *
 * Assumptions:
 * - Running Confluence
 * - App Diary installed
 *
 * Setup:
 * - Create a space with key "DR" and name "Diary"
 * - Create a page with title "macros" in the "DR" space
 * - Create 100 pages with the Diary macro and random macro parameters
 *
 * Virtual Users:
 * - DiaryEntryCreator: creates a diary entry in a random existing diary (on one of the 100 created pages)
 * - DiaryMacroPageReader: views a random page with the Diary macro (one of the 100 created pages)
 *
 * Sum of weight is 0.2 which leaves 0.8 for vanilla virtual users.
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