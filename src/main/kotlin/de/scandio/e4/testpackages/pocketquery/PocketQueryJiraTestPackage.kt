package de.scandio.e4.testpackages.pocketquery.pqconf

import de.scandio.e4.testpackages.pocketquery.pqjira.PocketQueryJiraSetupAction
import de.scandio.e4.worker.client.ApplicationName
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.TestPackage

/**
 * === PocketQuery for Confluence Test Package ===
 *
 * TODO: docs!
 *
 * @author Felix Grund
 */
class PocketQueryJiraTestPackage: TestPackage {

    val APP_LICENSE = System.getenv("E4_PLUGIN_LICENSE")

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()

        return virtualUsers
    }

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
//        actions.add(InstallPluginAction("pocketquery-jira", "1.3.1", APP_LICENSE, "de.scandio.jira.plugins.pocketquery-jira"))
        //actions.add(PocketQueryJiraSetupAction()) // TODO look at this
        // add create project
        // add pq settings in project
        return actions
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.jira
    }

}