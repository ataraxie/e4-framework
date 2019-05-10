package de.scandio.e4

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import org.openqa.selenium.WebDriver

class MainKotlin(
        val driver: WebDriver,
        val confluence: WebConfluence,
        val dom: DomHelper
) {

    fun execute() {
        createPage()
    }

    fun createPage() {
        val spaceKey: String = "E44"
        val parentPageTitle: String = "E4 Home"
        val pageTitle: String = "E4 Test Page"

        confluence.login("admin", "admin")
        confluence.takeScreenshot("after-login")
        confluence.goToDashboard()
        confluence.takeScreenshot("dashboard")
    }

    fun createSpace() {
        val spaceKey: String = "E4"
        val spaceName: String = "E4 Space"

        confluence.login("admin", "admin")
        confluence.takeScreenshot("after-login")
        confluence.goToDashboard()
        confluence.takeScreenshot("dashboard")
        dom.click("#addSpaceLink")
        confluence.takeScreenshot("createspace")
        dom.awaitElementPresent("li.template.selected")
        confluence.takeScreenshot("createspace-2")
        dom.click("#create-dialog .create-dialog-create-button")
        confluence.takeScreenshot("createspace-3")
        dom.awaitElementPresent("form.common-space-form")
        confluence.takeScreenshot("createspace-4")
        dom.insertText("form.common-space-form input[name='name']", spaceName)
        dom.clearText("form.common-space-form input[name='spaceKey']")
        confluence.takeScreenshot("createspace-5")
        dom.insertText("form.common-space-form input[name='spaceKey']", spaceKey)
        confluence.takeScreenshot("createspace-6")
        val createButtonSelector = "#create-dialog .create-dialog-commonPage + .dialog-button-panel .create-dialog-create-button"
        dom.awaitAttributeNotPresent(createButtonSelector, "disabled")
        confluence.takeScreenshot("createspace-7")
        dom.click(createButtonSelector)
        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]", 20)
        confluence.takeScreenshot("createspace-8")
    }

}