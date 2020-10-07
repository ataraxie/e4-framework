package de.scandio.e4.uitest.livelytheme

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import org.junit.After
import org.junit.Test
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LivelyTheme_3_2_0 : BaseSeleniumTest() {

    val SPACEKEY = "TEST"


    @Test
    fun generic_3_2_0() {
        val restConfluence = restClient() as RestConfluence
        val webConfluence = webClient() as WebConfluence
        val dom = webConfluence.dom
        val pageTitle = "LT 3.2.0 Complex Sample Page (${Date().time})"
        restConfluence.createPage(SPACEKEY, pageTitle, COMPLEX_SAMPLE_PAGE_XHTML)

        webConfluence.login()
        webConfluence.goToPage(SPACEKEY, pageTitle)
        webConfluence.debugScreen("AfterPageCreated")

        dom.awaitElementPresent("#main-content")
        assertNumElements(6, ".lively-widget.background-style-white")
        assertNumElements(9, ".lively-widget.background-style-colored")
        assertNumElements(6, ".lively-widget.text-style-dark")
        assertNumElements(3, ".lively-widget.text-style-light")
        assertNumElements(13, ".lively-widget.widget-fixed-height")
        assertNumElements(6, ".lively-widget .color-bar")
    }

    @Test // PRODUCTS-925 widget colors (flat style)
    fun products_925() {
        val webConfluence = webClient() as WebConfluence
        val pageTitle = "PRODUCTS-925 (${Date().time})"
        webConfluence.login()
        webConfluence.createPageKeepOpen(SPACEKEY, pageTitle)
        webConfluence.insertMacro("lively-widget", "Widget", mapOf(
                "title" to "PRODUCTS-925",
                "backgroundStyle" to "colored",
                "color" to "#bada55"
        ))
        webConfluence.insertMacroBody("lively-widget", "<p>TEST</p>")
        webConfluence.savePageOrBlogPost()
        webConfluence.debugScreen("products_925")
        assertOneElement(".lively-widget.background-style-colored.text-style-dark")
        assertHasContent(".lively-widget .title", "PRODUCTS-925")
        assertNoElement(".lively-widget .color-bar")
        assertBackgroundColor(".lively-widget > section", "#bada55")
    }

    @Test // PRODUCTS-1318 widget height
    fun products_1318_no_scroll() {
        val webConfluence = webClient() as WebConfluence
        val dom = webConfluence.dom
        val pageTitle = "PRODUCTS-1318 (${Date().time})"
        webConfluence.login()
        webConfluence.createPageKeepOpen(SPACEKEY, pageTitle)
        webConfluence.insertMacro("lively-widget", "Widget", mapOf(
                "title" to "PRODUCTS-1318",
                "height" to "100px"
        ))
        webConfluence.insertMacroBody("lively-widget", "<p>TEST</p>")
        webConfluence.savePageOrBlogPost()
        webConfluence.debugScreen("products_1318_no_scroll")
        assertHasStyles(".lively-widget section", mapOf(
                "height" to "100px"
        ))
        assertFalse(dom.isScrollbarVisible(".lively-widget .wiki-content"))
    }

    @Test // PRODUCTS-1318 widget height
    fun products_1318_scroll() {
        val webConfluence = webClient() as WebConfluence
        val dom = webConfluence.dom
        val pageTitle = "PRODUCTS-1318 (${Date().time})"
        webConfluence.login()
        webConfluence.createPageKeepOpen(SPACEKEY, pageTitle)
        webConfluence.insertMacro("lively-widget", "Widget", mapOf(
                "title" to "PRODUCTS-1318",
                "height" to "100px"
        ))
        webConfluence.insertMacroBody("lively-widget", "<p>TEST</p><p>TEST</p><p>TEST</p><p>TEST</p>")
        webConfluence.savePageOrBlogPost()
        webConfluence.debugScreen("products_1318_scroll")
        assertHasStyles(".lively-widget section", mapOf(
                "height" to "100px"
        ))
        assertTrue(dom.isScrollbarVisible(".lively-widget .wiki-content"))
    }

    @Test // PRODUCTS-376 custom css class
    fun products_376() {
        val webConfluence = webClient() as WebConfluence
        val pageTitle = "PRODUCTS-376 (${Date().time})"
        val dom = webConfluence.dom
        webConfluence.login()
        webConfluence.createPageKeepOpen(SPACEKEY, pageTitle)
        webConfluence.openMacroBrowser("lively-widget", "Widget")
        webConfluence.setMacroParameters(mapOf("title" to "PRODUCTS-376"))
        dom.scrollToBottom(".macro-input-fields")
        dom.click(".macro-param-div .aui-button.aui-button-link")
        dom.scrollToBottom(".macro-input-fields")
        webConfluence.setMacroParameters(mapOf("customClass" to "e4class"))
        webConfluence.saveMacroBrowser()
        webConfluence.savePageOrBlogPost()
        assertOneElement(".lively-widget-custom-e4class")
        webConfluence.debugScreen("products_376")
    }

    @After
    fun after() {
        webClient().quit()
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