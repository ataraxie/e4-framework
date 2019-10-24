package de.scandio.e4.testpackages.vanilla

import de.scandio.e4.testpackages.vanilla.virtualusers.*
import de.scandio.e4.worker.client.ApplicationName
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.collections.VirtualUserCollection

/**
 * === VanillaTestPackage ===
 *
 * Test package for vanilla Confluence virtual users.
 *
 * Assumptions:
 * - Running Confluence
 *
 * Setup:
 * - NONE
 *
 * Virtual Users:
 * Commentor (weight 0.08) - creates comments
 * Reader (weight 0.36) - reads pages and blog posts
 * Creator (weight 0.08) - creates pages and blogposts
 * Searcher (weight 0.16) - uses the confluence search
 * Editor (weight 0.16) - edits pages and blogposts
 * Dashboarder (weight 0.16) - visits the dashboard
 *
 * Sum of weight is 1.0. Weights will be downscaled by other test packages.
 *
 * @author Felix Grund
 */
class VanillaTestPackage: TestPackage {

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        return actions
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
        virtualUsers.add(Commentor::class.java, 0.12)
        virtualUsers.add(Reader::class.java, 0.32)
        virtualUsers.add(Creator::class.java, 0.12)
        virtualUsers.add(Searcher::class.java, 0.16)
        virtualUsers.add(Editor::class.java, 0.12)
        virtualUsers.add(Dashboarder::class.java, 0.16)
        return virtualUsers
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }

}