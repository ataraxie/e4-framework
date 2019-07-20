package de.scandio.e4.testpackages.vanilla

import de.scandio.e4.E4TestEnv
import de.scandio.e4.testpackages.TestPackageTestRun
import de.scandio.e4.testpackages.vanilla.actions.*
import org.junit.Before
import org.junit.Test

class VanillaTestRun : TestPackageTestRun() {

    private val TEST_PACKAGE = VanillaTestPackage()

    @Before
    fun before() {
        //noop currently
    }

    @Test
    fun runTest() {
        if (E4TestEnv.PREPARATION_RUN) {
            executeTestPackagePrepare(TEST_PACKAGE)
        } else {
//                executeTestPackage(TEST_PACKAGE)

            // Run a single action for testing:
             executeAction(ViewDashboardAction())

            // Run single virtual user for testing:
            // executeActions(BranchCreator().actions)
        }
    }

}