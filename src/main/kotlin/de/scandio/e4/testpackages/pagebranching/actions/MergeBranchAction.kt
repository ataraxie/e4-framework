package de.scandio.e4.testpackages.pagebranching.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.pagebranching.PageBranchingSeleniumHelper
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
        val helper = PageBranchingSeleniumHelper(webClient)

        webConfluence.login()

        if (originPageTitle.equals("PLACEHOLDER")) {
            super.originPageTitle = "MergeBranchAction (${Date().time})"
            webConfluence.createPageAndSave(spaceKey, originPageTitle)
        }
        if (branchName.equals("PLACEHOLDER")) {
            super.branchName = "Branch (${Date().time})"
            helper.createBranchFromCurrentlyOpenPage(branchName)
        }

        this.start = Date().time
        helper.mergeCurrentlyOpenBranchPage()
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

}