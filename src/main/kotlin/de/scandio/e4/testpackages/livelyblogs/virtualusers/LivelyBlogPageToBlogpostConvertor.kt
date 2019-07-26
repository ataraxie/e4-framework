package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.livelyblogs.actions.ConvertRandomPageToBlogPost
import de.scandio.e4.testpackages.livelyblogs.actions.SearchBlogpostOverview
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser

/**
 * === LivelyBlogPageToBlogpostConvertor ===
 *
 * Lively Blogs LivelyBlogPageToBlogpostConvertor VirtualUser.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space with key "LB"
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - Navigates a random page in space "LB"
 * - Clicks on Lively Blog "convert page to blog post" button in action menu
 * - Converts the current page to a blog post (without deleting the page)
 *
 * @author Felix Grund
 */
class LivelyBlogPageToBlogpostConvertor : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(ConvertRandomPageToBlogPost("LB"))
        return actions
    }

}
