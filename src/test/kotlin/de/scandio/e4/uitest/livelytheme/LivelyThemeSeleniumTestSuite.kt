package de.scandio.e4.uitest.livelytheme

import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.assertEquals


// REQUIRES:
// - Lively Theme installed
// - Confluence user admin/admin (if not configured differently with envvars)
//
// If you want the setup to run, set E4_PREPARATION_RUN envvar to true.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LivelyThemeSeleniumTestSuite : AbstractLivelyThemeTestSuite() {

    @Test
    fun test_generic_complex_sample_page() {
        runWithDump {
            val pageTitle = "LT Complex Sample Page ${Date().time}"
            restConfluence.createPage(SPACEKEY, pageTitle, COMPLEX_SAMPLE_PAGE_XHTML)

            webConfluence.goToPage(SPACEKEY, pageTitle)

            dom.awaitElementPresent("#main-content")
            assertNumElements(6, ".lively-widget.background-style-white")
            assertNumElements(9, ".lively-widget.background-style-colored")
            assertNumElements(6, ".lively-widget.text-style-dark")
            assertNumElements(3, ".lively-widget.text-style-light")
            assertNumElements(13, ".lively-widget.widget-fixed-height")
            assertNumElements(6, ".lively-widget .color-bar")
        }
    }

    @Test
    fun test_custom_dashboard_page() {
        runWithDump {
            testCustomElementConfiguredInAdmin("dashboard", ".lively-custom-dashboard")
        }
    }

    @Test
    fun test_custom_footer_page() {
        runWithDump {
            testCustomElementConfiguredInAdmin("footer", ".lively-custom-footer")
        }
    }

    @Test
    fun test_custom_header_page() {
        runWithDump {
            testCustomElementConfiguredInAdmin("header", ".lively-custom-header")
        }
    }

    @Test
    fun test_custom_menu_page() {
        runWithDump {
            testCustomElementConfiguredInAdmin("menu", ".lively-custom-menu") {
                dom.click("#lively-custom-menu-link")
                dom.awaitMilliseconds(200)
            }
        }
    }

    @Test
    fun test_custom_submenu_page() {
        runWithDump {
            testCustomElementConfiguredInAdmin("submenu", ".lively-custom-submenu")
        }
    }

    @Test
    fun test_favorites_menu_configured_in_admin() {
        runWithDump {
            helper.goToSettings()
            helper.clickTab("favouritesMenu")
            helper.activateToggle("favouritesMenu")
            helper.saveSettings()

            webConfluence.goToDashboard()
            dom.expectElementPresent("#lively-favouritesMenu-link")

            helper.goToSettings()
            helper.clickTab("favouritesMenu")
            helper.deactivateToggle("favouritesMenu")
            helper.saveSettings()

            webConfluence.goToDashboard()
            dom.expectElementNotPresent("#lively-favouritesMenu-link")
        }
    }

    private fun testCustomElementConfiguredInAdmin(
            elementKey: String, customContainerSelector: String, beforeCustomElementCheckHook: () -> Unit = {}) {
        val pageTitle = "LT $elementKey ${Date().time}"
        restConfluence.createPage(SPACEKEY, pageTitle, "<p>content-$elementKey</p>")

        helper.goToSettings()
        helper.clickTab(elementKey)
        helper.activateToggle(elementKey)

        dom.awaitSeconds(3) // give the index some time for the created page

        helper.pickFirstPageReturnValue(elementKey, pageTitle)
        val color = helper.pickRandomColorReturnValue(elementKey)
        helper.saveSettings()

        webConfluence.goToDashboard()

        beforeCustomElementCheckHook()

        val livelyContainer = dom.findElement(customContainerSelector)
        assertEquals("content-$elementKey", livelyContainer.text)
        assertBackgroundColor(customContainerSelector, color)

        helper.goToSettings()
        helper.clickTab(elementKey)
        helper.deactivateToggle(elementKey)
        helper.saveSettings()

        webConfluence.goToDashboard()
        dom.expectElementNotPresent(customContainerSelector)
    }

    val COMPLEX_SAMPLE_PAGE_XHTML = """
<ac:layout><ac:layout-section ac:type="single"><ac:layout-cell><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#FF5630</ac:parameter><ac:parameter ac:name="title">White widget</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"/></p></ac:rich-text-body></ac:structured-macro><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#00B8D9</ac:parameter><ac:parameter ac:name="backgroundStyle">colored</ac:parameter><ac:parameter ac:name="title">Colored Widget</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"/></p></ac:rich-text-body></ac:structured-macro></ac:layout-cell></ac:layout-section><ac:layout-section ac:type="three_equal"><ac:layout-cell><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#6554C0</ac:parameter><ac:parameter ac:name="title">Height 315</ac:parameter><ac:parameter ac:name="height">315px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"><ac:parameter ac:name="">1</ac:parameter></ac:structured-macro></p></ac:rich-text-body></ac:structured-macro><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#6554C0</ac:parameter><ac:parameter ac:name="title">Height 315</ac:parameter><ac:parameter ac:name="height">315px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"><ac:parameter ac:name="">1</ac:parameter></ac:structured-macro></p></ac:rich-text-body></ac:structured-macro></ac:layout-cell><ac:layout-cell><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#006644</ac:parameter><ac:parameter ac:name="backgroundStyle">colored</ac:parameter><ac:parameter ac:name="title">Height 200</ac:parameter><ac:parameter ac:name="height">200px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"><ac:parameter ac:name="">1</ac:parameter></ac:structured-macro></p></ac:rich-text-body></ac:structured-macro><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#006644</ac:parameter><ac:parameter ac:name="backgroundStyle">colored</ac:parameter><ac:parameter ac:name="title">Height 200</ac:parameter><ac:parameter ac:name="height">200px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"><ac:parameter ac:name="">1</ac:parameter></ac:structured-macro></p></ac:rich-text-body></ac:structured-macro><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#006644</ac:parameter><ac:parameter ac:name="backgroundStyle">colored</ac:parameter><ac:parameter ac:name="title">Height 200</ac:parameter><ac:parameter ac:name="height">200px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"><ac:parameter ac:name="">1</ac:parameter></ac:structured-macro></p></ac:rich-text-body></ac:structured-macro></ac:layout-cell><ac:layout-cell><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#FF5630</ac:parameter><ac:parameter ac:name="title">Height 660</ac:parameter><ac:parameter ac:name="height">660px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"/></p></ac:rich-text-body></ac:structured-macro></ac:layout-cell></ac:layout-section><ac:layout-section ac:type="single"><ac:layout-cell><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#eee</ac:parameter><ac:parameter ac:name="backgroundStyle">colored</ac:parameter><ac:parameter ac:name="height">300px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"/></p></ac:rich-text-body></ac:structured-macro></ac:layout-cell></ac:layout-section><ac:layout-section ac:type="two_right_sidebar"><ac:layout-cell><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#FF8B00</ac:parameter><ac:parameter ac:name="backgroundStyle">colored</ac:parameter><ac:parameter ac:name="title">Main</ac:parameter><ac:parameter ac:name="height">430px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"/></p></ac:rich-text-body></ac:structured-macro></ac:layout-cell><ac:layout-cell><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#FFE380</ac:parameter><ac:parameter ac:name="backgroundStyle">colored</ac:parameter><ac:parameter ac:name="title">Side 1</ac:parameter><ac:parameter ac:name="height">200px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"><ac:parameter ac:name="">1</ac:parameter></ac:structured-macro></p></ac:rich-text-body></ac:structured-macro><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#FFE380</ac:parameter><ac:parameter ac:name="backgroundStyle">colored</ac:parameter><ac:parameter ac:name="title">Side 2</ac:parameter><ac:parameter ac:name="height">200px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"><ac:parameter ac:name="">1</ac:parameter></ac:structured-macro></p></ac:rich-text-body></ac:structured-macro></ac:layout-cell></ac:layout-section><ac:layout-section ac:type="two_left_sidebar"><ac:layout-cell><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#998DD9</ac:parameter><ac:parameter ac:name="title">Side 1</ac:parameter><ac:parameter ac:name="height">200px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"><ac:parameter ac:name="">1</ac:parameter></ac:structured-macro></p></ac:rich-text-body></ac:structured-macro><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#998DD9</ac:parameter><ac:parameter ac:name="title">Side 2</ac:parameter><ac:parameter ac:name="height">200px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"><ac:parameter ac:name="">1</ac:parameter></ac:structured-macro></p></ac:rich-text-body></ac:structured-macro></ac:layout-cell><ac:layout-cell><ac:structured-macro ac:name="lively-widget"><ac:parameter ac:name="color">#998DD9</ac:parameter><ac:parameter ac:name="backgroundStyle">colored</ac:parameter><ac:parameter ac:name="title">Main</ac:parameter><ac:parameter ac:name="height">430px</ac:parameter><ac:rich-text-body>
<p><ac:structured-macro ac:name="loremipsum"/></p></ac:rich-text-body></ac:structured-macro></ac:layout-cell></ac:layout-section></ac:layout>
    """.trimIndent().trimLines()

}