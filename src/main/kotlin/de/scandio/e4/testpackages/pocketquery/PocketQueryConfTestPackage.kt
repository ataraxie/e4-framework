package de.scandio.e4.testpackages.pocketquery

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
class PocketQueryConfTestPackage: TestPackage {

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
        return virtualUsers
    }

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        //actions.add(InstallPluginAction("pocketquery", E4TestEnv.APP_VERSION, E4TestEnv.APP_LICENSE, "de.scandio.confluence.plugins.pocketquery"))
//        actions.add(CreateSpaceAction("PQ", "PocketQuery", true))
        return actions
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }

}