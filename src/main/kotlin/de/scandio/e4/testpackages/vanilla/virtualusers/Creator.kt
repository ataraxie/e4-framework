package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.VirtualUser

/**
 * Confluence Creator VirtualUser.
 *
 * Assumptions:
 * TODO: list assumptions
 *
 * Actions:
 * TODO: list actions
 *
 * @author Felix Grund
 */
class Creator : VirtualUser {

    override fun getActions(): MutableList<Action> {
        val actions = ActionCollection()
        TODO("Add Scenarios")
//        val spaceKey = "E4${getRandomNumber(1, 999)}"
//        val spaceName = "01 E4 Space ${getRandomNumber(1, 999)}"
//        var pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
//        actions.add(CreateSpaceAction(spaceKey, spaceName))
//        actions.add(CreatePageAction(spaceKey, pageTitle))
//        pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
//        actions.add(CreatePageAction(spaceKey, pageTitle))
//        pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
//        actions.add(CreatePageAction(spaceKey, pageTitle))
//        pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
//        actions.add(CreatePageAction(spaceKey, pageTitle))
//        pageTitle = "E4 Page ${getRandomNumber(1, 999)}"
//        actions.add(CreatePageAction(spaceKey, pageTitle))
        return actions
    }

    fun getRandomNumber(min: Int, max: Int): Long {
        return Math.round(Math.random() * (max - min + 1) + min)
    }

}