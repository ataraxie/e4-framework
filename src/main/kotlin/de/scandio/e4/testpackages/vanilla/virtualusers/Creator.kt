package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.CreateRandomPageAction
import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient

/**
 * Confluence Creator VirtualUser.
 *
 * Actions:
 * - Creates a page as child of a random page with text and macro content.
 *
 * @author Felix Grund
 */
class Creator : VirtualUser() {

    override fun onInit(restClient: RestClient) {
    }

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(CreateRandomPageAction("Creator"))
        return actions
    }


}