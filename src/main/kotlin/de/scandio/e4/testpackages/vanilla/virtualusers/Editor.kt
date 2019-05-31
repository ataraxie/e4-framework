package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * Confluence Editor VirtualUser.
 *
 * Assumptions:
 * TODO: list assumptions
 *
 * Actions:
 * TODO: list actions
 *
 * @author Felix Grund
 */
class Editor : VirtualUser {

    override fun getActions(webClient: WebClient, restClient: RestClient): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewRandomContent())
        return actions
    }
}