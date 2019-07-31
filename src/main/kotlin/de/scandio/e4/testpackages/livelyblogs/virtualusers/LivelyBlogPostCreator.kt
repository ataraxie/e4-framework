package de.scandio.e4.testpackages.livelyblogs.virtualusers

import de.scandio.e4.testpackages.livelyblogs.actions.CreateRandomLivelyBlogPost
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser

/**
 * === LivelyBlogPostCreator ===
 *
 * Lively Blogs LivelyBlogPostCreator VirtualUser.
 *
 * Assumptions:
 * - Lively Blogs app installed
 * - Space with key "LB"
 * - Page with title "Lively Blog Home" in space with key "LB"
 * - Multiple images attached to page "Lively Blog Home"
 *
 * Preparation:
 * - NONE
 *
 * Actions:
 * - Creates a blog posts in space with key "LB"
 * - With a chance of 33%, takes an image attached to page "Lively Blog Home" and uses it as teaser image
 *
 * @author Felix Grund
 */
class LivelyBlogPostCreator : VirtualUser() {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(CreateRandomLivelyBlogPost("LB", "Lively Blog Home"))
        return actions
    }

}
