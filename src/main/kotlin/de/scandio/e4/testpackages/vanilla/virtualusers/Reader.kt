package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.*
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * Confluence Reader Action.
 *
 * Actions:
 * - Views a random Confluence page.
 *
 * @author Felix Grund
 */
class Reader : VirtualUser {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewRandomContent())
        return actions
    }
}