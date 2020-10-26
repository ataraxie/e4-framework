package de.scandio.e4.uitest.diary

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import org.junit.After
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

// REQUIRES:
// - Diary 1.5.0+ installed
// - space with key "TEST"
// - Confluence user admin/admin (if not configured differently with envvars)
class Diary_1_5_0 : BaseSeleniumTest() {

    val SPACEKEY = "TEST"

    @Test
    fun DRCSRV_31_welcome_message_not_hidden() {
        runWithDump {
            val webConfluence = webClient() as WebConfluence
            val dom = webConfluence.dom
            createPageWithDiaryMacro()
            dom.expectElementPresent(".entry.welcome-message")
        }
    }

    @Test
    fun DRCSRV_31_welcome_message_hidden() {
        runWithDump {
            val webConfluence = webClient() as WebConfluence
            val dom = webConfluence.dom
            createPageWithDiaryMacro(mapOf("hidewelcomemessage" to "true"))
            dom.expectElementNotPresent(".entry.welcome-message")
        }
    }

    @Test
    fun DRCSRV_12_share_button_one_entry() {
        runWithDump {
            val webConfluence = webClient() as WebConfluence
            val dom = webConfluence.dom
            createPageWithDiaryMacro()
            createRandomDiaryEntry()
            dom.hoverOverElement("li.entry.focused")
            dom.click("li.entry.focused .share-button")
            dom.awaitElementVisible("#aui-flag-container")
            val titleElement = dom.findElement("#aui-flag-container .title")
            assertEquals("Your entry is ready to be shared!", titleElement.text)
        }
    }

    fun createRandomDiaryEntry() {
        val webConfluence = webClient() as WebConfluence
        val dom = webConfluence.dom
        dom.click(".quick-editor-prompt")
        dom.awaitElementPresent("#wysiwygTextarea_ifr")
        webConfluence.insertMarkdown("Random content ${Date().time}")
        dom.removeElementWithJQuery(".aui-blanket")
        dom.awaitMilliseconds(200)
        dom.click("#rte-button-publish")
        dom.awaitElementClickable("li.entry.focused")
    }

    fun createPageWithDiaryMacro(macroParameters: Map<String, String> = mapOf()) {
        val restConfluence = restClient() as RestConfluence
        val webConfluence = webClient() as WebConfluence
        val dom = webConfluence.dom
        val pageTitle = "Diary 1.5.0 DRCSRV-31 Test Page (${Date().time})"
        restConfluence.createPage(SPACEKEY, pageTitle, "")
        webConfluence.login()
        webConfluence.goToPage(SPACEKEY, pageTitle)
        webConfluence.goToEditCurrentPage()
        webConfluence.insertMacro("diary", "Diary", macroParameters)
        webConfluence.savePageOrBlogPost()
        dom.awaitElementPresent(".sc-diary")
    }

    @After
    fun after() {
        webClient().quit()
    }

}