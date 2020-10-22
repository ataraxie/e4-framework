package de.scandio.e4.testpackages.livelytheme

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.WorkerUtils
import java.util.*

class LivelyThemeSeleniumHelper(
        protected val webClient: WebClient
) {

    val webConfluence = webClient as WebConfluence
    val dom = webConfluence.dom

    fun setLivelyThemeGlobally() {
        webConfluence.setGlobalConfluenceTheme("lively")
    }

    fun setDefaultThemeGlobally() {
        webConfluence.setGlobalConfluenceTheme("default")
    }

    fun goToSettings() {
        webConfluence.navigateTo("/admin/plugins/lively/theme/settings.action")
        dom.awaitElementPresent(".screenshot")
    }

    fun clickToggle(key: String) {
        dom.click("#tabs-$key.active-pane aui-toggle")
    }

    fun clickToggleTwice(key: String) {
        clickToggle(key)
        dom.awaitSeconds(1)
        clickToggle(key)
        dom.awaitSeconds(1)
    }

    fun clickTab(elementKey: String) {
        dom.click(".menu-item a[href=\"#tabs-$elementKey\"]")
        dom.awaitSeconds(1)
        dom.awaitElementClickable("#tabs-$elementKey.active-pane aui-toggle")
    }

    fun pickFirstPageReturnValue(elementKey: String, pageTitle: String = "Lively"): String {
        val inputSelector = "#tabs-$elementKey.active-pane .content-page input"
        dom.insertText(inputSelector, pageTitle, true)
        dom.awaitMilliseconds(200)
        dom.click("$inputSelector + .autocomplete li:first-child a")
        dom.awaitMilliseconds(200)
        return dom.findElement(inputSelector).getAttribute("value")
    }

    fun pickRandomColorReturnValue(elementKey: String): String {
        val randomIndex = WorkerUtils.getRandomItem(listOf(1,2,3,4,5,6))
        val containerSelector = "#tabs-$elementKey.active-pane"
        dom.click("$containerSelector .palette-color-picker-button")
        dom.awaitMilliseconds(300)
        dom.click("$containerSelector .swatch:nth-child($randomIndex)")
        dom.awaitMilliseconds(300)
        return dom.findElement("$containerSelector .color-picker-input").getAttribute("value")
    }

    fun activateToggle(elementKey: String) {
        webConfluence.activateAuiToggle("#tabs-$elementKey.active-pane aui-toggle")
    }

    fun deactivateToggle(elementKey: String) {
        webConfluence.deactivateAuiToggle("#tabs-$elementKey.active-pane aui-toggle")
    }

    fun saveSettings() {
        dom.click(".settings-lively-theme .aui-button.submit")
    }

}