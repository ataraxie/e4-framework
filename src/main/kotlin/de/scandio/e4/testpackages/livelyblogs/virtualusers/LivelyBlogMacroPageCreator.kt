package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.livelyblogs.actions.CreateRandomLivelyBlogMacroPage
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * === LivelyBlogMacroPageCreator ===
 *
 * Lively Blogs LivelyBlogMacroPageCreator VirtualUser.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space with key "LB"
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - Creates pages in the given space containing the Lively Blog Posts macro with
 *   random macro parameters in the page content
 *
 * @author Felix Grund
 */
class LivelyBlogMacroPageCreator : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(CreateRandomLivelyBlogMacroPage("LB"))
        return actions
    }

}
