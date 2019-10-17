package de.scandio.e4.testpackages.livelyblogs

import de.scandio.e4.testpackages.gitsnippets.virtualusers.GitSnippetsMacroPageCreator
import de.scandio.e4.testpackages.gitsnippets.virtualusers.GitSnippetsMacroPageReader
import de.scandio.e4.testpackages.livelyblogs.actions.*
import de.scandio.e4.testpackages.livelyblogs.virtualusers.*
import de.scandio.e4.testpackages.vanilla.actions.CreateMultiplePagesActionRest
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.CreateSpaceAction
import de.scandio.e4.testpackages.vanilla.virtualusers.*
import de.scandio.e4.worker.client.ApplicationName
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.TestPackage

/**
 * === LivelyBlogsTestPackage ===
 *
 * Test package for app "Lively Blogs for Confluence".
 *
 * Assumptions:
 * - Running Confluence
 * - App Lively blogs is installed
 *
 * Setup:
 * - Create space with key "LB" and name "Lively Blog" (REST)
 * - Create 100 simple child pages of "Lively Blog" (REST)
 * - Set space key "LB" for featured blog posts in Lively Blogs administration (SELENIUM)
 * - Add 5 colors, categories "category{1,5}", and labels "label{1,5}" in Lively Blogs category administration
 * - Upload all images with names "random-images-*.jpg" from E4_INPUT_DIR to page "Lively Blog Home"
 *   of space with key "LB"
 * --- If images are not present in E4_INPUT_DIR, copy the images from $classpath/images
 * - Create a page with title "macros" in space with key "LB"
 * - Create 50 child pages of "macros" page with Lively Blog Posts macro and
 *   random macro parameters in page content
 * - Create 100 blog posts in space with key "LB"
 * --- With a chance of 20%, add label "important" to blog post (marking post as prioritized)
 * --- With a chance of 33%, add an image from "Lively Blog Home" to the blog post content and
 *     set it as teaser image
 * --- With a chance of 50%, add one or more labels "label{1,5}" to the blogpost (marking post as categorized)
 *
 * Virtual Users:
 * - LivelyBlogNavigator (weight 0.1): views the Blogs overview and navigates all the
 *     available tabs ("All", "Featured", category-specific).
 * - LivelyBlogSearcher (weight 0.04): searches for blogposts with the Lively Blog search
 * - LivelyBlogMacroPageCreator (weight 0.04): creates pages with the Lively Blog macro with
 *     randomized parameters
 * - LivelyBlogMacroPageReader (weight 0.08): reads pages with the lively-blog-posts
 *     macro (pages are created during setup)
 * - LivelyBlogPostCreator (weight 0.04): creates blogposts and randomly adds teaser images and labels
 * - LivelyBlogPageToBlogpostConvertor (weight 0.04): converts pages to blogposts
 *
 * Sum of weight is 0.34 which leaves 0.66 for vanilla virtual users.
 *
 * @author Felix Grund
 */
class LivelyBlogsTestPackage: TestPackage {

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(CreateSpaceAction("LB", "Lively Blog", true))
        actions.add(CreateMultiplePagesActionRest("LB", "Lively Blog Home", 100))
        actions.add(SetupSetSpaceForFeaturedPosts())
        actions.add(SetupLivelyBlogCategories())
        actions.add(UploadAllImages("LB", "Lively Blog Home", "random-image.*\\.jpg"))
        actions.add(CreatePageAction("LB", "macros", "<p>macro pages</p>", true))
        actions.add(SetupLivelyBlogMacroPagesAction("LB", "macros", 50))
        actions.add(SetupLivelyBlogPostsAction("LB", "Lively Blog Home", 100))
        return actions
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
        // 0.66
        virtualUsers.add(Commentor::class.java, 0.08)
        virtualUsers.add(Reader::class.java, 0.26)
        virtualUsers.add(Creator::class.java, 0.08)
        virtualUsers.add(Searcher::class.java, 0.08)
        virtualUsers.add(Editor::class.java, 0.08)
        virtualUsers.add(Dashboarder::class.java, 0.08)

        // 0.34
        virtualUsers.add(LivelyBlogNavigator::class.java, 0.1)
        virtualUsers.add(LivelyBlogSearcher::class.java, 0.04)
        virtualUsers.add(GitSnippetsMacroPageCreator::class.java, 0.04)
        virtualUsers.add(GitSnippetsMacroPageReader::class.java, 0.08)
        virtualUsers.add(LivelyBlogPostCreator::class.java, 0.04)
        virtualUsers.add(LivelyBlogPageToBlogpostConvertor::class.java, 0.04)
        return virtualUsers
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }

}