package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.livelyblogs.actions.ViewRandomBlogpostOverview
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser

/**
 * === LivelyBlogNavigator ===
 *
 * Lively Blogs LivelyBlogNavigator VirtualUser.
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
 * - Navigates to the global Lively Blog overview
 * - Clicks on a random button in the Lively Blog navigation
 * - Waits until the respective blog posts are shown
 *
 * @author Felix Grund
 */
class LivelyBlogNavigator : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewRandomBlogpostOverview())
        return actions
    }

}
