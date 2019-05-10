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
//        createSpace()
        createPage()
    }

    fun createPage() {
        val loremIpsum = "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."
        val spaceKey: String = "E4"
        val parentPageTitle: String = "E4 Home"
        val pageTitle: String = "E4 Test Page"

        confluence.login("admin", "admin")
        confluence.takeScreenshot("after-login")
        confluence.goToSpaceHomepage(spaceKey)
        confluence.takeScreenshot("spacehomepage")
        dom.click("#quick-create-page-button")
        dom.awaitElementPresent("#wysiwyg")
        confluence.takeScreenshot("create-page-1")
        dom.click("#content-title-div")
        dom.insertText("#content-title", "TEST")
        confluence.takeScreenshot("create-page-2")
        dom.click("#wysiwygTextarea_ifr")
        dom.insertTextTinyMce("<h1>Lorem Ipsum</h1><p>$loremIpsum</p>")
        confluence.takeScreenshot("create-page-3")
        dom.click("#rte-button-insert")
        dom.click("#rte-insert-macro")
        confluence.takeScreenshot("create-page-4")
        dom.awaitElementPresent("#macro-browser-dialog[aria-hidden]")
        confluence.takeScreenshot("create-page-5")
        dom.insertText("#macro-browser-search", "info")
        dom.awaitElementPresent("#macro-info")
        confluence.takeScreenshot("create-page-6")
        dom.click("#macro-info")
        dom.awaitElementPresent("#macro-param-title")
        dom.insertText("#macro-param-title", "E4 Info")
        confluence.takeScreenshot("create-page-7")
        dom.click("#macro-details-page button.ok")
        dom.await(2000) // TODO: condition!
        confluence.takeScreenshot("create-page-8")
        dom.click("#rte-button-publish")
        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]")
        confluence.takeScreenshot("create-page-9")
    }

    fun getRandomNumber(min: Int, max: Int): Double {
        return Math.floor(Math.random() * (max - min + 1) + min)
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