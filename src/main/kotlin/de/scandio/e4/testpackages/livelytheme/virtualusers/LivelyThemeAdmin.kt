package de.scandio.e4.testpackages.livelytheme.virtualusers

import de.scandio.e4.testpackages.livelytheme.actions.SetRandomCustomElement
import de.scandio.e4.testpackages.vanilla.actions.ViewPageAction
import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * === LivelyThemeAdmin ===
 *
 * LivelyTheme LivelyThemeAdmin VirtualUser.
 *
 * Assumptions:
 * - Lively Theme app installed
 * - Lively Theme set as global theme
 * - Space with spacekey "LT"
 * - Page with title "macros" in space "LT"
 * - Test pages with LivelyTheme macros as child pages of "macros" page
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - Set a random custom Lively Theme element in Theme Settings and create the associated page through the
 *   Lively Theme UI (i.e. using the different custom element edit buttons in the frontend)
 *
 * @author Felix Grund
 */
open class LivelyThemeAdmin : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(SetRandomCustomElement("LT"))
        return actions
    }

    override fun isAdminRequired(): Boolean {
        return true
    }

}