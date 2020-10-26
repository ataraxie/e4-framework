package de.scandio.e4.uitest.pagebranching

import de.scandio.e4.E4Env
import de.scandio.e4.worker.util.RandomData
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import org.junit.runners.MethodSorters
import org.springframework.core.annotation.Order
import java.io.File
import java.util.*
import kotlin.test.assertEquals

// REQUIRES:
// - PageBranching installed
// - Confluence user admin/admin (if not configured differently with envvars)
//
// If you want the setup to run, set E4_PREPARATION_RUN envvar to true.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PageBranching_1_7_0 : AbstractPageBranchingTestSuite() {

    companion object {

        var ID_A = 0L
        var ID_B = 0L
        var ID_C = 0L
        var ID_A1 = 0L
        var ID_A2 = 0L
        var ID_A3 = 0L
        var ID_B1 = 0L
        var ID_B2 = 0L
        var ID_B3 = 0L
        var ID_C1 = 0L
        var ID_C2 = 0L
        var ID_C3 = 0L
        lateinit var ATT_D: File
        lateinit var ATT_E: File
        lateinit var ATT_F: File
        lateinit var ATT_G: File
        lateinit var ATT_H: File
        lateinit var ATT_I: File
        lateinit var ATT_J: File

        @BeforeClass
        @JvmStatic internal fun beforeAll() {
            runWithDump {
                setupAttachments()
                webConfluence.login()
                if (E4Env.PREPARATION_RUN) {
                    runWithDump {
                        restConfluence.createSpace(SPACEKEY, SPACENAME)
                        setupPages()
                    }
                }
            }
        }

        fun setupAttachments() {
            val images = webConfluence.prepareImages("random-image-[0-6].jpg")
            ATT_D = images[0]
            ATT_E = images[1]
            ATT_F = images[2]
            ATT_G = images[3]
            ATT_H = images[4]
            ATT_I = images[5]
            ATT_J = images[6]
        }

        fun setupPages() {
            ID_A = restConfluence.createPage(SPACEKEY, "A", RandomData.STRING_LOREM_IPSUM)
            ID_A1 = restHelper.createBranch(ID_A, "A1")
            webConfluence.addRandomContentToPage(ID_A1)
            ID_A2 = restHelper.createBranch(ID_A, "A2")
            webConfluence.uploadSingleImage(ID_A2, ATT_D)
            ID_A3 = restHelper.createBranch(ID_A, "A3")

            ID_B = restConfluence.createPage(SPACEKEY, "B", RandomData.STRING_LOREM_IPSUM)
            ID_B1 = restHelper.createBranch(ID_B, "B1")
            webConfluence.addRandomContentToPage(ID_B1)
            webConfluence.uploadSingleImage(ID_B1, ATT_E)
            ID_B2 = restHelper.createBranch(ID_B, "B2")
            webConfluence.addRandomContentToPage(ID_B2)
            ID_B3 = restHelper.createBranch(ID_B, "B3")
            webConfluence.uploadSingleImage(ID_B3, ATT_F)

            ID_C = restConfluence.createPage(SPACEKEY, "C", RandomData.STRING_LOREM_IPSUM)
            webConfluence.uploadSingleImage(ID_C, ATT_H)

            ID_C1 = restHelper.createBranch(ID_C, "C1")
            webConfluence.uploadSingleImage(ID_C1, ATT_H)
            webConfluence.uploadSingleImage(ID_C1, ATT_I)

            ID_C2 = restHelper.createBranch(ID_C, "C2")
            webConfluence.uploadSingleImage(ID_C1, ATT_J)

            ID_C3 = restHelper.createBranch(ID_C, "C3")
        }

    }

    @Test // Tests the case when bulk merge fails due to conflicting attachments
    fun test_1_error_conflicting_attachment() {
        runWithDump {
            webConfluence.goToAttachmentsOverview(ID_C)
            webConfluence.deleteAttachment(ATT_H.name)
            webConfluence.uploadSingleImage(ATT_I)
            webConfluence.renameAttachment(ATT_I.name, ATT_H.name)

            webConfluence.goToAttachmentsOverview(ID_C1)
            webConfluence.deleteAttachment(ATT_H.name)
            webConfluence.uploadSingleImage(ATT_G)
            webConfluence.renameAttachment(ATT_G.name, ATT_H.name)

            webHelper.goToBranchesPage(SPACEKEY)
            bulkMergeBranches(arrayListOf(ID_C1))
            awaitAttachmentConflictFlag()
            expectRowHasError(ID_C1)

            webConfluence.goToDashboard()
        }
    }

    @Test // Tests that nothing is merged when there is at least one error
    fun test_2_some_selected_errors() {
        runWithDump {
            webHelper.goToBranchesPage(SPACEKEY)
            bulkMergeBranches(arrayListOf(ID_C1, ID_A1))
            awaitAttachmentConflictFlag()
            expectRowHasError(ID_C1)
            expectRowChecked(ID_C1)
            expectRowHasNoStatus(ID_A1)
            webConfluence.goToDashboard()
        }
    }

    @Test // Tests scenario when all is merged but some branches have errors
    fun test_3_all_selected_errors() {
        runWithDump {
            webHelper.goToBranchesPage(SPACEKEY)
            dom.click("#bulk-select-all")
            bulkMergeBranches()
            awaitContentConflictFlag()
            expectRowHasNoStatus(ID_A1)
            expectRowHasNoStatus(ID_A2)
            expectRowHasNoStatus(ID_A3)
            // FIXME:
//            expectRowHasError(ID_B1)
//            expectRowHasError(ID_B2)
//            expectRowHasError(ID_B3)
            expectRowHasNoStatus(ID_B1)
            expectRowHasNoStatus(ID_B2)
            expectRowHasNoStatus(ID_B3)
            expectRowHasError(ID_C1)
            expectRowHasError(ID_C2)
            expectRowHasError(ID_C3)
            webConfluence.goToDashboard()
        }
    }

    @Test // Tests scenario some branches are merged successfully
    fun test_4_merge_some_success() {
        runWithDump {
            webHelper.goToBranchesPage(SPACEKEY)
            bulkMergeBranches(arrayListOf(ID_A1, ID_A2))
            webConfluence.awaitSuccessFlag()
            expectRowHasSuccess(ID_A1)
            expectRowHasSuccess(ID_A2)
            webConfluence.goToDashboard()
        }
    }

    @Test // Another successful merge
    fun test_5_another_merge_success() {
        runWithDump {
            webHelper.goToBranchesPage(SPACEKEY)
            bulkMergeBranches(arrayListOf(ID_B1, ID_B3, ID_C1, ID_C3))
            webConfluence.awaitSuccessFlag()
            expectRowHasSuccess(ID_B1)
            expectRowHasSuccess(ID_B3)
            expectRowHasSuccess(ID_C1)
            expectRowHasSuccess(ID_C3)
            webConfluence.goToDashboard()
        }
    }

    @Test // Only two remaining branches that cannot be merged
    fun test_6_only_two_remaining_branches_with_errors() {
        runWithDump {
//            webHelper.goToBranchesPage(SPACEKEY)
//            val numRows = dom.findElements("tr[data-page-id]").size
//            assertEquals(2, numRows)
//            expectRowPresent(ID_B2)
//            expectRowPresent(ID_C2)
//
//            bulkMergeBranches(arrayListOf(ID_B2, ID_C2))
//            awaitContentConflictFlag()
//            expectRowHasError(ID_B2)
//            expectRowHasError(ID_C2)
//
//            bulkMergeBranches(arrayListOf(ID_B2))
//            expectRowHasError(ID_B2)
//
//            bulkMergeBranches(arrayListOf(ID_C2))
//            expectRowHasError(ID_C2)
        }
    }

//    @Test
//    fun temp_test() {
//        webHelper.goToBranchesPage("E4PB1603562057678")
//        bulkMergeBranches(arrayListOf(32244628, 32244631))
//        webConfluence.awaitSuccessFlag()
//        expectRowHasSuccess(32244628)
//        expectRowHasSuccess(32244631)
//    }

    private fun expectRowPresent(branchPageId: Long) {
        dom.expectElementPresent("tr[data-page-id=\"${branchPageId}\"]")
    }

    private fun expectRowHasNoStatus(branchPageId: Long) {
        expectRowStatus(branchPageId, "not-processed")
    }

    private fun expectRowChecked(branchPageId: Long) {
        dom.expectElementPresent("tr[data-page-id=\"${branchPageId}\"] .checkbox:checked")
    }

    private fun expectRowHasError(branchPageId: Long) {
        expectRowStatus(branchPageId, "conflict")
    }

    private fun expectRowHasSuccess(branchPageId: Long) {
        expectRowStatus(branchPageId, "success")
    }

    private fun expectRowStatus(branchPageId: Long, status: String) {
        dom.expectElementPresent("tr[data-page-id=\"${branchPageId}\"][data-status=\"${status}\"]")
    }

    fun bulkMergeBranches(branchPageIds: List<Long> = arrayListOf()) {
        if (!branchPageIds.isEmpty()) {
            branchPageIds.forEach {
                dom.click("tr[data-page-id=\"${it}\"] .checkbox")
            }
        }
        dom.click("#bulk-merge-button")
        if (branchPageIds.size > 1) {
            webConfluence.removeAuiBlanket()
            dom.awaitMilliseconds(500)
            if (dom.findElement("#bulk-merge-branch-confirmation-dialog .pagebranching-merge-link").isDisplayed) {
                dom.click("#bulk-merge-branch-confirmation-dialog .pagebranching-merge-link", 0)
            }
        }
    }

    fun awaitContentConflictFlag() {
        webConfluence.awaitErrorFlag("Content conflict")
    }

    fun awaitAttachmentConflictFlag() {
        webConfluence.awaitErrorFlag("Attachment conflict")
    }

}