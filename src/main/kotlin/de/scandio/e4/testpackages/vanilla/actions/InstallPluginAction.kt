package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.clients.web.WebAtlassian
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

class InstallPluginAction(
        val pluginName: String,
        val pluginVersion: String,
        val pluginLicense: String = "",
        val pluginKey: String = ""
) : Action() {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webAtlassian = webClient as WebAtlassian
        webAtlassian.login()
        this.start = Date().time
        webAtlassian.installPlugin(pluginName, pluginVersion, pluginLicense, pluginKey)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}