package de.scandio.e4.testpackages.pagebranching.actions

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

/**
 * === MergeBranchAction ===
 *
 * PageBranching MergeBranch action.
 *
 * Assumptions:
 * - Space with key $spaceKey
 * - A branched page with title $branchedPageTitle
 *
 * Procedure (SELENIUM):
 * - View page with title $branchedPageTitle in space $spaceKey
 * - Click "Tools" > "Merge"
 * - When popup visible, click "Merge and trash" button
 * - Wait till origin page is visible
 *
 * Result:
 * - Branch is merged into original page
 *
 * @author Felix Grund
 */
open class MergeBranchAction (
        val spaceKey: String,
        var branchName: String,
        val originPageTitle: String
    ) : Action {

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = DomHelper(webConfluence)

        webConfluence.login()
        webConfluence.goToPage(spaceKey, "$branchName: $originPageTitle")

        this.start = Date().time
        dom.click("#action-menu-link")
        dom.awaitElementClickable(".pagebranching-merge-link")
        dom.click(".pagebranching-merge-link")
        dom.awaitElementClickable("#merge-branch-confirmation-dialog-submit-and-archive-button")
        dom.click("#merge-branch-confirmation-dialog-submit-and-archive-button")
        dom.awaitElementPresent(".page-branching-original-meta")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}