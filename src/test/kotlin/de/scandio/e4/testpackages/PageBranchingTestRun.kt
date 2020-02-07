package de.scandio.e4.testpackages

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.pagebranching.PageBranchingTestPackage
import de.scandio.e4.testpackages.pagebranching.virtualusers.*
import org.junit.Before
import org.junit.Test

class PageBranchingTestRun : TestPackageTestRun() {

    private val TEST_PACKAGE = PageBranchingTestPackage()

    @Before
    fun before() {
        // noop currently
    }

    @Test
    fun runTest() {
        if (E4Env.PREPARATION_RUN) {
            executeTestPackagePrepare(TEST_PACKAGE)
        } else {
            executeActions(BranchCreator().actions)
            executeActions(BranchMerger().actions)
            executeActions(BranchOverviewCreator().actions)
            executeActions(BranchOverviewReader().actions)
            executeActions(BranchedPageReader().actions)
            executeActions(DiffViewer().actions)

            // Run a single action for testing:
            // executeAction(CreatePageAction("MYSPACEKEY", "MYPAGETITLE"))

            // Run single virtual user for testing:
            // executeActions(BranchCreator().actions)
        }
    }

}