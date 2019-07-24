package de.scandio.e4.testpackages.livelyblogs

import de.scandio.e4.testpackages.livelyblogs.actions.*
import de.scandio.e4.testpackages.livelyblogs.virtualusers.*
import de.scandio.e4.testpackages.vanilla.actions.CreateMultiplePagesActionRest
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.CreateSpaceAction
import de.scandio.e4.testpackages.vanilla.actions.InstallPluginAction
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
 *
 * Setup:
 * - Install Lively Blogs app (SELENIUM)
 * - Create space with key "LB" and name "Lively Blogs" (REST)
 * - Create page with title "macros" in space "LB" (REST)
 * - Create 100 child pages of "macros" page in space "LT" (containing random Lively Theme macros) (REST)
 *
 * Virtual Users:
 * - LivelyMacroPageReader (weight 0.2): reads random pages with Lively Theme macros
 * - LivelyMacroPageCreator (weight 0.04): creates random pages with Lively Theme macros
 * - LivelyThemeAdmin (weight 0.02): sets random custom theme elements in Lively Theme global settings
 * - LivelySpaceToggler (weight 0.04): toggles space favorites
 * - LivelyPageToggler 0.04 (weight ): toggles page favorites
 *
 * Sum of weight is 0.34 which leaves 0.66 for vanilla virtual users.
 *
 * @author Felix Grund
 */
class LivelyBlogsTestPackage: TestPackage {

    val LICENSE = System.getenv("E4_APP_LICENSE")
    val PLUGIN_KEY = "de.scandio.confluence.plugins.lively-blog"

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(InstallPluginAction("lively-blog", "3.7.0", LICENSE, PLUGIN_KEY))
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
        virtualUsers.add(LivelyBlogMacroPageCreator::class.java, 0.04)
        virtualUsers.add(LivelyBlogMacroPageReader::class.java, 0.08)
        virtualUsers.add(LivelyBlogPostCreator::class.java, 0.04)
        virtualUsers.add(LivelyBlogPageToBlogpostConvertor::class.java, 0.04)
        return virtualUsers
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }

}