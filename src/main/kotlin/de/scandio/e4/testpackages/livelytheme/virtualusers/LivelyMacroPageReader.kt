package de.scandio.e4.testpackages.livelytheme.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * === LivelyMacroPageReader ===
 *
 * LivelyTheme LivelyMacroPageReader VirtualUser.
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
 * - View random page containing Lively Theme macro and associated page content
 *
 * @author Felix Grund
 */
open class LivelyMacroPageReader : VirtualUser() {
    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewRandomContent("LT", "macros"))
        return actions
    }

}