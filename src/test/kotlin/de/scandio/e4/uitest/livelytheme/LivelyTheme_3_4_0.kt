package de.scandio.e4.uitest.livelytheme

import org.junit.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertTrue

// REQUIRES:
// - Lively Theme installed
// - Confluence user admin/admin (if not configured differently with envvars)
//
// If you want the setup to run, set E4_PREPARATION_RUN envvar to true.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LivelyTheme_3_4_0 : AbstractLivelyThemeTestSuite() {

    val SPACE_PAGE_REGEX = Regex(".*:.*")
    val COLOR_REGEX = Regex("#.{6}")

    @Test // LTCSRV-22 Allow user to edit settings when Lively Theme is not globally enabled
    fun LTCSRV_22() {
        runWithDump {
            helper.setDefaultThemeGlobally()
            helper.goToSettings()
            dom.expectElementPresent("#lively-theme-not-selected-message")

            helper.setLivelyThemeGlobally()
            helper.goToSettings()
            dom.expectElementNotPresent("#lively-theme-not-selected-message")
        }
    }

    @Test // LTCSRV-21 Settings revamp: check all tabs are there in correct order and dashboard is selected initially
    fun LTCSRV_21_check_tabs_and_order() {
        runWithDump {
            helper.goToSettings()

            // check all tabs are there in correct order
            expectTabsAreInPosition(arrayListOf("dashboard", "header", "menu", "submenu", "favouritesMenu", "footer"))

            // check that first tab (dashboard tab is selected)
            expectTabActive("dashboard")
            expectToggleVisible("dashboard")
            expectToggleNotVisible("footer")

        }
    }

    @Test // LTCSRV-21 Settings revamp: click all tabs and aui-toggles
    fun LTCSRV_21_click_through_tabs() {
        runWithDump {
            helper.goToSettings()

            testTabNavigation("dashboard")
            testTabNavigation("header")
            testTabNavigation("menu")
            testTabNavigation("submenu")
            testTabNavigation("favouritesMenu")
            testTabNavigation("footer")
        }
    }

    @Test // LTCSRV-21 Settings revamp: test that the page pickers generally work
    fun LTCSRV_21_test_page_pickers() {
        runWithDump {
            helper.goToSettings()

            testPagePicker("dashboard")
            testPagePicker("header")
            testPagePicker("menu")
            testPagePicker("submenu")
            testPagePicker("footer")
        }
    }

    @Test // LTCSRV-21 Settings revamp: test that the color pickers generally work
    fun LTCSRV_21_test_color_pickers() {
        runWithDump {
            helper.goToSettings()

            testColorPicker("dashboard")
            testColorPicker("header")
            testColorPicker("menu")
            testColorPicker("submenu")
            testColorPicker("footer")
        }
    }

    @Test // LTCSRV-21 Settings revamp: test the favorites menu panel only has a toggle
    fun LTCSRV_21_test_favorites_menu_has_only_toggle() {
        runWithDump {
            helper.goToSettings()

            helper.clickTab("favouritesMenu")
            dom.expectElementPresent("#tabs-favouritesMenu.active-pane .enable-element:last-child")
        }
    }

    @Test // LTCSRV-30 settings screenshots
    fun LTCSRV_30_screenshots() {
        runWithDump {
            helper.goToSettings()

            clickTabAndExpectScreenshotVisible("dashboard")
            clickTabAndExpectScreenshotVisible("header")
            clickTabAndExpectScreenshotVisible("menu")
            clickTabAndExpectScreenshotVisible("submenu")
            clickTabAndExpectScreenshotVisible("favouritesMenu")
            clickTabAndExpectScreenshotVisible("footer")
        }
    }

    private fun clickTabAndExpectScreenshotVisible(elementKey: String) {
        helper.clickTab(elementKey)
        expectScreenshotVisible(elementKey)
    }

    private fun testTabNavigation(elementKey: String, clickTab: Boolean = true) {
        if (clickTab) {
            helper.clickTab(elementKey)
        }
        helper.clickToggleTwice(elementKey)
    }

    private fun testColorPicker(elementKey: String, clickTab: Boolean = true) {
        if (clickTab) {
            helper.clickTab(elementKey)
        }
        val inputValue = helper.pickRandomColorReturnValue(elementKey)
        assertTrue(inputValue.matches(COLOR_REGEX))
    }

    private fun testPagePicker(elementKey: String, clickTab: Boolean = true) {
        if (clickTab) {
            helper.clickTab(elementKey)
        }
        val inputValue = helper.pickFirstPageReturnValue(elementKey)
        assertTrue(inputValue.matches(SPACE_PAGE_REGEX))
    }




}