package de.scandio.e4.testpackages

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.diary.TeamAdminTestPackage
import de.scandio.e4.testpackages.gitsnippets.virtualusers.DiaryEntryCreator
import org.junit.Before
import org.junit.Test

class DiaryTestRun : TestPackageTestRun() {

    private val TEST_PACKAGE = TeamAdminTestPackage()

    @Before
    fun before() {
        // noop currently
    }

    @Test
    fun runTest() {
        if (E4Env.PREPARATION_RUN) {
            executeTestPackagePrepare(TEST_PACKAGE)
        } else {
            executeActions(DiaryEntryCreator().actions)
//            executeActions(DiaryMacroPageReader().actions)

            // Run a single action for testing:
            // executeAction(CreatePageAction("MYSPACEKEY", "MYPAGETITLE"))

            // Run single virtual user for testing:
            // executeActions(BranchCreator().actions)
        }
    }

}