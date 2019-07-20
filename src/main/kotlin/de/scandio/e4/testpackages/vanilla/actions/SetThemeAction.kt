package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class SetThemeAction(
        val themeKey: String
) : Action() {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        webConfluence.login()
        this.start = Date().time
        webConfluence.navigateTo("admin/choosetheme.action")
        dom.awaitElementClickable("#choosethemeform")
        dom.click("[id$='$themeKey']")
        dom.click(".submit.aui-button")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}