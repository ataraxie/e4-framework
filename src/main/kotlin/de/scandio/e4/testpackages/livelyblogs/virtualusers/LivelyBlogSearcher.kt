package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.livelyblogs.actions.SearchBlogpostOverview
import de.scandio.e4.testpackages.livelyblogs.actions.ViewRandomBlogpostOverview
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser

/**
 * === LivelyBlogSearcher ===
 *
 * Lively Blogs LivelyBlogSearcher VirtualUser.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space with key "LB" and name "Lively Blog"
 * - Blog posts with labels "label{1,9}" in space with key "LB"
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - Navigate to the blog post overview
 * - Search for blog posts in space with key "LB" with a random label "label{1,5}"
 *
 * @author Felix Grund
 */
class LivelyBlogSearcher : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(SearchBlogpostOverview("LB", "Lively Blog"))
        return actions
    }

}
