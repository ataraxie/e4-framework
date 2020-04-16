package de.scandio.e4.adhoc

import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import org.junit.After
import org.junit.Test
import java.util.*
import kotlin.test.assertTrue

class SampleAdhocTest : BaseSeleniumTest() {

    @Test
    fun testMacro() {
        val webConfluence = webClient() as WebConfluence
        val restConfluence = restClient() as RestConfluence

        try {
            val pageTitle = "E4 Info Macro Test (${Date().time})"
            val pageId = restConfluence.createPage("E4", pageTitle, pageMetadataContent, "E4 Home")
            webConfluence.login()
            webConfluence.goToPage(pageId)
            webConfluence.goToEditPage()
            webConfluence.insertMacro("info", "info", mapOf(
                    "title" to "E4 Provided Title",
                    "icon" to "false"
            ))
            webConfluence.savePageOrBlogPost()
            val renderedMacroPresent = webConfluence.dom.isElementPresent(".confluence-information-macro.has-no-icon")
            assertTrue(renderedMacroPresent)
        } catch (e: Exception) {
            shot()
            quit()
            throw e
        } finally {
            shot()
            quit()
        }
    }

    @After
    fun after() {
        webClient().quit()
    }

    val pageMetadataContent = """
        <h1>E4 Test Headline</h1>
        <p>Some page content. Nothing special.</p>
    """.trimIndent().trimLines()

}