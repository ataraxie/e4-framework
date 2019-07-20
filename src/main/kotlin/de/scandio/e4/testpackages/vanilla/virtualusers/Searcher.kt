package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.*
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * Confluence Searcher VirtualUser.
 *
 * Actions:
 * - Searches for random lorem ipsum words on the Confluence search page.
 *
 * @author Felix Grund
 */
class Searcher : VirtualUser() {

    override fun onInit(restClient: RestClient) {
    }

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(SearchLoremIpsumAction())
        return actions
    }
}