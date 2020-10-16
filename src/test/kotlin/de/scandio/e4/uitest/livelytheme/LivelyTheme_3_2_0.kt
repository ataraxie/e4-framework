package de.scandio.e4.uitest.livelytheme

import org.junit.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// REQUIRES:
// - Lively Theme installed
// - Confluence user admin/admin (if not configured differently with envvars)
//
// If you want the setup to run, set E4_PREPARATION_RUN envvar to true.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LivelyTheme_3_2_0 : AbstractLivelyThemeTestSuite() {

    @Test // PRODUCTS-925 widget colors (flat style)
    fun products_925() {
        runWithDump {
            webConfluence.createPageKeepOpen(SPACEKEY, "PRODUCTS-925")
            webConfluence.insertMacro("lively-widget", "Widget", mapOf(
                    "title" to "PRODUCTS-925",
                    "backgroundStyle" to "colored",
                    "color" to "#bada55"
            ))
            webConfluence.insertMacroBody("lively-widget", "<p>TEST</p>")
            webConfluence.savePageOrBlogPost()
            assertOneElement(".lively-widget.background-style-colored.text-style-dark")
            assertHasContent(".lively-widget .title", "PRODUCTS-925")
            assertNoElement(".lively-widget .color-bar")
            assertBackgroundColor(".lively-widget > section", "#bada55")
        }
    }

    @Test // PRODUCTS-1318 widget height
    fun products_1318_no_scroll() {
        runWithDump {
            webConfluence.createPageKeepOpen(SPACEKEY, "PRODUCTS-1318")
            webConfluence.insertMacro("lively-widget", "Widget", mapOf(
                    "title" to "PRODUCTS-1318",
                    "height" to "100px"
            ))
            webConfluence.insertMacroBody("lively-widget", "<p>TEST</p>")
            webConfluence.savePageOrBlogPost()
            assertHasStyles(".lively-widget section", mapOf(
                    "height" to "100px"
            ))
            assertFalse(dom.isScrollbarVisible(".lively-widget .wiki-content"))
        }
    }

    @Test // PRODUCTS-1318 widget height
    fun products_1318_scroll() {
        runWithDump {
            webConfluence.createPageKeepOpen(SPACEKEY, "PRODUCTS-1318")
            webConfluence.insertMacro("lively-widget", "Widget", mapOf(
                    "title" to "PRODUCTS-1318",
                    "height" to "100px"
            ))
            webConfluence.insertMacroBody("lively-widget", "<p>TEST</p><p>TEST</p><p>TEST</p><p>TEST</p>")
            webConfluence.savePageOrBlogPost()
            assertHasStyles(".lively-widget section", mapOf(
                    "height" to "100px"
            ))
            assertTrue(dom.isScrollbarVisible(".lively-widget .wiki-content"))
        }
    }

    @Test // PRODUCTS-376 custom css class
    fun products_376() {
        runWithDump {
            webConfluence.createPageKeepOpen(SPACEKEY, "PRODUCTS-376")
            webConfluence.openMacroBrowser("lively-widget", "Widget")
            webConfluence.setMacroParameters(mapOf("title" to "PRODUCTS-376"))
            dom.scrollToBottom(".macro-input-fields")
            dom.click(".macro-param-div .aui-button.aui-button-link")
            dom.scrollToBottom(".macro-input-fields")
            webConfluence.setMacroParameters(mapOf("customClass" to "e4class"))
            webConfluence.saveMacroBrowser()
            webConfluence.savePageOrBlogPost()
            assertOneElement(".lively-widget-custom-e4class")
        }
    }

}