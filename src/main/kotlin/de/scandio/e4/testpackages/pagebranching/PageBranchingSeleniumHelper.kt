package de.scandio.e4.testpackages.pagebranching

import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.worker.interfaces.WebClient

class PageBranchingSeleniumHelper(
        protected val webClient: WebClient
) {

    val webConfluence = webClient as WebConfluence
    val dom = webConfluence.dom

    fun createBranchFromCurrentlyOpenPage(branchName: String): Long {
        val dom = DomHelper(webConfluence.driver)
        dom.awaitMilliseconds(100)
        dom.click("#action-menu-link")
        dom.awaitMilliseconds(100)
        dom.click(".pagebranching-create-branch-link")
        dom.insertText("input#branch-name", branchName, true)
        dom.click("#pagebranching-branch-page-button")
        dom.awaitMilliseconds(300)
        dom.awaitElementPresent(".page-branching-branch-meta")
        return webConfluence.getPageId()
    }

    fun mergeCurrentlyOpenBranchPage() {
        dom.click("#action-menu-link")
        dom.click("#action-menu .pagebranching-merge-link")
        dom.click("#merge-branch-confirmation-dialog .pagebranching-merge-link")
        dom.awaitElementPresent(".page-metadata-modification-info")
    }

    fun addOverviewMacroToPage(spaceKey: String, pageTitle: String) {
        webConfluence.goToPage(spaceKey, pageTitle)
        webConfluence.goToEditCurrentPage()
        webConfluence.insertMacro("page-branching-overview", "page branching")
        webConfluence.savePageOrBlogPost()
    }

    fun editCurrentlyOpenBranchAndShowDiff() {
        webConfluence.editCurrentlyOpenPageAddRandomContent()
        dom.click("#content-metadata-pagebranching")
        dom.click("a.pagebranching-viewdiff-link")
        dom.awaitElementPresent("#num-changes-container .haschanges .count")
    }

    fun goToBranchesPage(spaceKey: String) {
        webConfluence.goToPage(spaceKey, "Branches")
        dom.awaitSeconds(1)
    }

}