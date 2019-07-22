package de.scandio.e4.testpackages.livelyblogs

import de.scandio.e4.testpackages.livelyblogs.actions.SetupLivelyBlogPostsAction
import de.scandio.e4.testpackages.livelyblogs.virtualusers.LivelyBlogAdministrator
import de.scandio.e4.testpackages.livelyblogs.virtualusers.LivelyBlogMacroReader
import de.scandio.e4.testpackages.livelyblogs.virtualusers.LivelyBlogNavigator
import de.scandio.e4.testpackages.livelyblogs.virtualusers.LivelyBlogSearcher
import de.scandio.e4.testpackages.livelytheme.actions.ClearThemeSettingsAction
import de.scandio.e4.testpackages.livelytheme.actions.SetupLivelyThemeMacroPages
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.CreateSpaceAction
import de.scandio.e4.testpackages.vanilla.actions.InstallPluginAction
import de.scandio.e4.testpackages.vanilla.actions.SetThemeAction
import de.scandio.e4.testpackages.vanilla.virtualusers.*
import de.scandio.e4.worker.client.ApplicationName
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.collections.VirtualUserCollection
import de.scandio.e4.worker.interfaces.TestPackage

/**
 * === LivelyBlogTestPackage ===
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
class LivelyBlogTestPackage: TestPackage {

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
//        actions.add(InstallPluginAction("lively-blog", "3.7.0"))
//        actions.add(CreateSpaceAction("LB", "Lively Blog", true))
        actions.add(CreatePageAction("LB", "macros", "<p>macro pages</p>", true))
        actions.add(SetupLivelyBlogMacroPages("LB", "macros", 20))
        actions.add(SetupLivelyBlogPostsAction("LB", 100))
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
        virtualUsers.add(LivelyBlogNavigator::class.java, 0.04)
        virtualUsers.add(LivelyBlogSearcher::class.java, 0.04)
        virtualUsers.add(LivelyBlogAdministrator::class.java, 0.04)
        virtualUsers.add(LivelyBlogMacroReader::class.java, 0.04)

//        virtualUsers.add(LivelyMacroPageReader::class.java, 0.2)
//        virtualUsers.add(LivelyMacroPageCreator::class.java, 0.04)
//        virtualUsers.add(LivelyThemeAdmin::class.java, 0.02)
//        virtualUsers.add(LivelySpaceToggler::class.java, 0.04)
//        virtualUsers.add(LivelyPageToggler::class.java, 0.04)
        return virtualUsers
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }

}