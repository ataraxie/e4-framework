package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.AddRandomCommentAction
import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * Confluence Commentor VirtualUser.
 *
 * Actions:
 * - Adds a lorem ipsum comment on a random page with write access.
 *
 * @author Felix Grund
 */
class Commentor : VirtualUser {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(AddRandomCommentAction())
        return actions
    }
}