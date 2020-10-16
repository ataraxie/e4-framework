package de.scandio.e4.testpackages.pagebranching.actions

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.pagebranching.PageBranchingSeleniumHelper
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import java.util.*

/**
 * === CreateOverviewPageAction ===
 *
 * PageBranching CreateBranch action.
 *
 * Assumptions:
 * - Space with key $spaceKey
 * - Page with title $originPageTitle in space $spaceKey that has a few branches
 *
 * Procedure (SELENIUM):
 * - View page with title $originPageTitle in space $spaceKey
 * - Click "Edit" button
 * - When editor loaded, click "Insert" ("+") button > "Other macros"
 * - Search for macro using "page branching" string
 * - Click on macro in macro browser and click "Insert"
 * - Save page
 *
 * Result:
 * - Page with title "$originPageTitle" contains page-branching-overview macro at the top of the content
 *
 * @author Felix Grund
 */
class CreateOverviewPageAction(
        spaceKey: String,
        originPageTitle: String = "PLACEHOLDER",
        branchName: String = "PLACEHOLDER"
) : CreateBranchAction(spaceKey, originPageTitle, branchName) {

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val webConfluence = webClient as WebConfluence
        val helper = PageBranchingSeleniumHelper(webClient)
        webConfluence.login()
        if ("PLACEHOLDER".equals(originPageTitle)) {
            originPageTitle = "CreateOverviewPageAction (${Date().time})"
            webConfluence.createPageAndSave(spaceKey, originPageTitle)
        }
        if ("PLACEHOLDER".equals(branchName)) {
            branchName = "Branch (${Date().time})"
            helper.createBranchFromCurrentlyOpenPage(branchName)
        }

        this.start = Date().time
        helper.addOverviewMacroToPage(spaceKey, originPageTitle)
        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


}