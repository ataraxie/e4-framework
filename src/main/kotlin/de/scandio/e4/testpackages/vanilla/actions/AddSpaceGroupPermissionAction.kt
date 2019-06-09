package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.RandomData
import java.util.*

class AddSpaceGroupPermissionAction(
        val spaceKey : String,
        val groupName: String,
        val permissionKey: String,
        val shallBePermitted: Boolean
) : Action() {

    private var start: Long = 0
    private var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        webConfluence.login()
        this.start = Date().time
        webConfluence.addSpaceGroupPermission(spaceKey, groupName, permissionKey, shallBePermitted)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}