package de.scandio.e4.testpackages.pagebranching.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.pagebranching.PageBranchingSeleniumHelper
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

/**
 * === CreateBranchAction ===
 *
 * PageBranching CreateBranch action.
 *
 * Assumptions:
 * - Space with key $spaceKey
 * - Page with title $originPageTitle in space $spaceKey
 *
 * Procedure (SELENIUM):
 * - View page with title $originPageTitle in space $spaceKey
 * - Click "Tools" > "Create Branch"
 * - When popup visible, enter $branchName and click "Create Branch"
 * - Wait till branched page is visible
 *
 * Result:
 * - Page with title "$branchName: $originPageTitle" is created
 *
 * @author Felix Grund
 */
open class CreateBranchAction (
    val spaceKey: String,
    var originPageTitle: String = "PLACEHOLDER",
    var branchName: String = "PLACEHOLDER"
    ) : Action() {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val helper = PageBranchingSeleniumHelper(webClient)

        webConfluence.login()

        if (originPageTitle.equals("PLACEHOLDER")) {
            originPageTitle = "CreateBranchAction (${Date().time})"
            webConfluence.createPageAndSave(spaceKey, originPageTitle)
        }
        if (branchName.equals("PLACEHOLDER")) {
            branchName = "Branch (${Date().time})"
        }

        this.start = Date().time
        helper.createBranchFromCurrentlyOpenPage(branchName)
        this.end = Date().time
    }



    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}