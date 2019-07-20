package de.scandio.e4.testpackages.pagebranching.actions

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.clients.WebConfluence
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

        webConfluence.login()

        if (originPageTitle.equals("PLACEHOLDER")) {
            originPageTitle = "CreateBranchAction (${Date().time})"
            createOriginPage(webConfluence)
        }
        if (branchName.equals("PLACEHOLDER")) {
            branchName = "Branch (${Date().time})"
        }

        this.start = Date().time
        createBranch(webConfluence)
        this.end = Date().time
    }

    open fun createOriginPage(webConfluence: WebConfluence) {
        webConfluence.createDefaultPage(spaceKey, originPageTitle)
    }

    open fun createBranch(webConfluence: WebConfluence) {
        val dom = DomHelper(webConfluence.driver)
        webConfluence.goToPage(spaceKey, originPageTitle)
        dom.awaitElementClickable("#action-menu-link")
        dom.click("#action-menu-link")
        dom.awaitElementClickable(".pagebranching-create-branch-link")
        dom.click(".pagebranching-create-branch-link")
        dom.awaitElementClickable("input#branch-name")
        dom.clearText("input#branch-name")
        dom.insertText("input#branch-name", branchName)
        dom.click("#pagebranching-branch-page-button")
        dom.awaitMilliseconds(2000)
        dom.awaitElementPresent(".page-branching-branch-meta")
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}