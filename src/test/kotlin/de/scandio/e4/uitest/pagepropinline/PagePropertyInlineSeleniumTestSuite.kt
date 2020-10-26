package de.scandio.e4.uitest.pagepropinline

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import org.junit.After
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class PagePropertyInlineSeleniumTestSuite : BaseSeleniumTest() {

    init { // FIXME: need to find a way to do @BeforeAll/@BeforeClass
        val restConfluence = restClient() as RestConfluence
        if (!restConfluence.spaceExists("PP")) {
            restConfluence.createSpace("PP", "Page Property Inline")
        }
    }

    @Test
    fun testMacro() {
        val webConfluence = webClient() as WebConfluence
        val restConfluence = restClient() as RestConfluence

        try {
            val pageTitle = "E4 Page Property Inline Test (${Date().time})"
            val pageId = restConfluence.createPage("PP", pageTitle, pageMetadataContent, "Page Property Inline Home")
            webConfluence.login()
            webConfluence.goToPage(pageId)
            expectElementPresent(".plugin-tabmeta-details .confluenceTh")
            webConfluence.goToEditCurrentPage()
            webConfluence.insertMacro("page-property-inline", "page property inline", mapOf(
                    "key" to "Key1"
            ))
            webConfluence.savePageOrBlogPost()
            val valueElement = webConfluence.dom.findElement(".ppi-value")
            assertEquals("Value1", valueElement.text)
        } catch (e: Exception) {
            shot()
            quit()
            throw e
        } finally {
            shot()
            quit()
        }
    }

    fun expectElementPresent(selector: String) {
        val webConfluence = webClient() as WebConfluence
        webConfluence.dom.awaitElementPresent(selector, 1)
    }


    @After
    fun after() {
        webClient().quit()
    }

    val pageMetadataContent = """
<p class="auto-cursor-target">
    <ac:structured-macro ac:name="details" ac:schema-version="1" ac:macro-id="94d67da5-b2e5-420f-b71a-3a118b8116cf">
        <ac:rich-text-body>
            <table>
                <colgroup>
                    <col />
                    <col />
                </colgroup>
                <tbody>
                    <tr>
                        <th>Key1</th>
                        <td>Value1</td>
                    </tr>
                    <tr>
                        <th>Key2</th>
                        <td>Value2</td>
                    </tr>
                    <tr>
                        <th>Key3</th>
                        <td>Value3</td>
                    </tr>
                    <tr>
                        <th>Key4</th>
                        <td>Value4</td>
                    </tr>
                </tbody>
            </table>
        </ac:rich-text-body>
    </ac:structured-macro>
</p>
    """.trimIndent().trimLines()

}