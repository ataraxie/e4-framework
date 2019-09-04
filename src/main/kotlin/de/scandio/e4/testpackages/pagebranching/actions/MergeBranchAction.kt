package de.scandio.e4.testpackages.pagebranching.actions

import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.clients.web.WebConfluence
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
        spaceKey: String,
        originPageTitle: String = "PLACEHOLDER",
        branchName: String = "PLACEHOLDER"
    ) : CreateBranchAction(spaceKey, originPageTitle, branchName) {

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val dom = DomHelper(webConfluence.driver)

        webConfluence.login()

        if (originPageTitle.equals("PLACEHOLDER")) {
            super.originPageTitle = "MergeBranchAction (${Date().time})"
            super.createOriginPage(webConfluence)
        }
        if (branchName.equals("PLACEHOLDER")) {
            super.branchName = "Branch (${Date().time})"
            super.createBranch(webConfluence)
        }

        this.start = Date().time
        dom.click("#action-menu-link")
        dom.awaitElementClickable(".pagebranching-merge-link")
        webConfluence.debugScreen("mergebranch-1")
        dom.click(".pagebranching-merge-link")
        webConfluence.debugScreen("mergebranch-2")
        dom.awaitElementClickable("#merge-branch-confirmation-dialog-submit-and-archive-button")
        webConfluence.debugScreen("mergebranch-3")
        dom.click("#merge-branch-confirmation-dialog-submit-and-archive-button")
        webConfluence.debugScreen("mergebranch-4")
        dom.awaitElementPresent(".page-metadata-modification-info")
        webConfluence.debugScreen("mergebranch-5")
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}