package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser

/**
 * === LivelyBlogMacroPageReader ===
 *
 * Lively Blogs LivelyBlogMacroPageReader VirtualUser.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space with key "LB"
 * - Page with title "macros" in space "LB" with all child pages containing the Lively Blog Posts macro
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - Views a random child page (containing the Lively Blog Posts macro) of
 *   page with title "macros" in space with key "LB"
 *
 * @author Felix Grund
 */
class LivelyBlogMacroPageReader : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewRandomContent("LB", "macros", ".lively-blog-posts"))
        return actions
    }

}
