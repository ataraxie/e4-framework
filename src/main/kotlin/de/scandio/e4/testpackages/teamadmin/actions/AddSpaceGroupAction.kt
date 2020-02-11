package de.scandio.e4.testpackages.diary.actions

import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.WorkerUtils
import java.util.*

class AddSpaceGroupAction () : Action() {

    private val SPACEKEY: String = "TEST"

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = webConfluence.dom
        val newGroupName = "e4${Date().time}"

        webConfluence.login()

        this.start = Date().time
        webConfluence.navigateTo("/spaces/spacepermissions.action?key=${SPACEKEY}")
        dom.click("#spaceadmin-create-group-button")
        dom.insertText("#spaceadmin-create-group-name", newGroupName)
        val checkboxes = dom.findElements(".spaceadmin-permission-checkbox:not(#spaceadmin-permission-checkbox-all-view)")
        for (checkbox in checkboxes) {
            if (WorkerUtils.getRandomItem(listOf(1,2,3)) == 3) { // every third case
                dom.click(checkbox)
                dom.awaitMilliseconds(100)
            }
        }
        dom.click("#create-dialog-submit-button")
        dom.awaitElementClickable("#spaceadminedit-${SPACEKEY.toLowerCase()}-${newGroupName}")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}