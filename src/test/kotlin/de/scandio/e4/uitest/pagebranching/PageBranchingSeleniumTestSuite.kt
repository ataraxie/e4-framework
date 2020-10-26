package de.scandio.e4.uitest.pagebranching

import de.scandio.e4.E4Env
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

// REQUIRES:
// - PageBranching installed
// - Confluence user admin/admin (if not configured differently with envvars)
//
// If you want the setup to run, set E4_PREPARATION_RUN envvar to true.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PageBranchingSeleniumTestSuite : AbstractPageBranchingTestSuite() {

    companion object {
        @BeforeClass
        @JvmStatic internal fun beforeAll() {
            if (E4Env.PREPARATION_RUN) {
                runWithDump {
                    webConfluence.login()
                }
            }
        }
    }

    @Test
    fun test_create_branch() {
        runWithDump {
            webConfluence.createPageAndSave(SPACEKEY, "PB testBranchCreator Origin")
            webHelper.createBranchFromCurrentlyOpenPage("Branch 1")
            dom.expectElementPresent(".page-branching-branch-meta")
        }
    }

    @Test
    fun test_merge_branch() {
        runWithDump {
            webConfluence.createPageAndSave(SPACEKEY, "PB testBranchCreator Origin")
            webHelper.createBranchFromCurrentlyOpenPage("Branch 1")
            webHelper.mergeCurrentlyOpenBranchPage()
            dom.awaitSeconds(3) // FIXME: it fails when I remove this right now... need to inspect!
            dom.expectElementNotPresent(".page-branching-branch-meta")
        }
    }

    @Test
    fun test_create_branch_overview() {
        runWithDump {
            val originPageTitle = webConfluence.createPageAndSave(SPACEKEY, "PB testBranchOverviewCreator Origin")
            val pageId1 = webHelper.createBranchFromCurrentlyOpenPage("Branch 1")
            webConfluence.goToPage(SPACEKEY, originPageTitle)
            val pageId2 = webHelper.createBranchFromCurrentlyOpenPage("Branch 2")
            webConfluence.goToPage(SPACEKEY, originPageTitle)
            val pageId3 = webHelper.createBranchFromCurrentlyOpenPage("Branch 3")
            webHelper.addOverviewMacroToPage(SPACEKEY, originPageTitle)
            expectPageInOverviewTable(pageId1)
            expectPageInOverviewTable(pageId2)
            expectPageInOverviewTable(pageId3)
        }
    }

    @Test
    fun test_view_diff() {
        runWithDump {
            webConfluence.createPageAndSave(SPACEKEY, "PB testDiffViewer Origin")
            webHelper.createBranchFromCurrentlyOpenPage("Branch 1")
            webHelper.editCurrentlyOpenBranchAndShowDiff()
            val numChanges = dom.findElement("#num-changes-container .haschanges .count").text.toInt()
            assertEquals(1, numChanges)
            dom.expectElementPresent("#added-diff-0")
        }
    }

}