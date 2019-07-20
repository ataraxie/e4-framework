package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.EditRandomContent
import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.rest.RestConfluence


/**
 * Confluence Editor VirtualUser.
 *
 * Actions:
 * - Edits a random Confluence page by adding text of random length to its content.
 *
 * @author Felix Grund
 */
class Editor : VirtualUser() {

    override fun onInit(restClient: RestClient) {
        (restClient as RestConfluence).fillCachesIfEmpty()
    }

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(EditRandomContent())
        return actions
    }
}