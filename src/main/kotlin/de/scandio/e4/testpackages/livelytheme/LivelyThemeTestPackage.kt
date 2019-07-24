package de.scandio.e4.testpackages.livelytheme

import de.scandio.e4.testpackages.livelytheme.actions.ClearThemeSettingsAction
import de.scandio.e4.testpackages.livelytheme.actions.SetupLivelyThemeMacroPages
import de.scandio.e4.testpackages.livelytheme.virtualusers.*
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
 * === LivelyThemeTestPackage ===
 *
 * Test package for app "Lively Theme for Confluence".
 *
 * Assumptions:
 * - Running Confluence
 *
 * Setup:
 * - Install Lively Theme app (SELENIUM)
 * - Set Lively Theme theme as global Confluence theme (SELENIUM)
 * - Clear theme settings (SELENIUM)
 * - Create space with key "LT" and name "Lively Theme" (REST)
 * - Create page with title "macros" in space "LT" (REST)
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
class LivelyThemeTestPackage: TestPackage {

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(InstallPluginAction("lively-theme", "3.1.1"))
        actions.add(SetThemeAction("lively-theme"))
        actions.add(ClearThemeSettingsAction())
        actions.add(CreateSpaceAction("LT", "Lively Theme", true))
        actions.add(CreatePageAction("LT", "macros", "<p>macro pages</p>", true))
        actions.add(SetupLivelyThemeMacroPages("LT", "macros", 100, MACRO_PAGES))
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
        virtualUsers.add(LivelyMacroPageReader::class.java, 0.2)
        virtualUsers.add(LivelyMacroPageCreator::class.java, 0.04)
        virtualUsers.add(LivelyThemeAdmin::class.java, 0.02)
        virtualUsers.add(LivelySpaceToggler::class.java, 0.04)
        virtualUsers.add(LivelyPageToggler::class.java, 0.04)
        return virtualUsers
    }

    val LICENSE = System.getenv("E4_LIVELY_THEME_LICENSE")

    val PLUGIN_KEY = "de.scandio.confluence.plugins.lively-theme"

    val MACRO_PAGES = mapOf(
            "lively-button" to "<ac:structured-macro ac:name=\"lively-button\" ac:schema-version=\"1\"><ac:parameter ac:name=\"link\"><ac:link><ri:page ri:content-title=\"Lively Theme Home\" /></ac:link></ac:parameter><ac:parameter ac:name=\"text\">My Button</ac:parameter></ac:structured-macro>",
            "lively-column-width" to "<ac:layout><ac:layout-section ac:type=\"two_equal\"><ac:layout-cell><p><ac:structured-macro ac:name=\"lively-column-width\" ac:schema-version=\"1\" ac:macro-id=\"07c31d3d-7e4f-4581-9d57-35c827bdab3b\"><ac:parameter ac:name=\"width\">300px</ac:parameter></ac:structured-macro></p><p>Left</p></ac:layout-cell><ac:layout-cell><p><ac:structured-macro ac:name=\"lively-column-width\" ac:schema-version=\"1\" ac:macro-id=\"8a00473e-7d5b-408d-8533-ed11fddba477\"><ac:parameter ac:name=\"width\">600px</ac:parameter></ac:structured-macro></p><p>Right</p></ac:layout-cell></ac:layout-section></ac:layout>",
            "lively-widget" to "<ac:structured-macro ac:name=\"lively-widget\" ac:schema-version=\"1\"><ac:parameter ac:name=\"color\">#403294</ac:parameter><ac:rich-text-body><p>Test</p></ac:rich-text-body></ac:structured-macro>",
            "lively-menu" to "<ac:structured-macro ac:name=\"lively-menu\" ac:schema-version=\"1\" ac:macro-id=\"2588027f-c85c-4a06-86f4-f0f13b3882b6\"><ac:rich-text-body><ul><li>Test</li><li>Test2</li><li>Test3</li></ul></ac:rich-text-body></ac:structured-macro>",
            "lively-list" to "<ac:structured-macro ac:name=\"lively-list\" ac:schema-version=\"1\"><ac:parameter ac:name=\"type\">recentlyViewedSpaces</ac:parameter></ac:structured-macro>",
            "lively-margin" to "<ac:structured-macro ac:name=\"lively-margin\" ac:schema-version=\"1\"><ac:parameter ac:name=\"margin\">100px</ac:parameter><ac:rich-text-body><p>Test</p></ac:rich-text-body></ac:structured-macro>"
    )

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }

}