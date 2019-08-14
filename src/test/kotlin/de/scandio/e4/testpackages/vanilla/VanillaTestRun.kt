package de.scandio.e4.testpackages.vanilla

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.TestPackageTestRun
import org.junit.Before
import org.junit.Test

class VanillaTestRun : TestPackageTestRun() {

    @Before
    fun before() {
        // noop currently
    }

    @Test
    fun runTest() {
        val testPackage = VanillaTestPackage()
        if (E4Env.PREPARATION_RUN) {
            executeTestPackagePrepare(testPackage)
        } else {
            executeTestPackage(testPackage)

            // Run a single action for testing:
            // executeAction(ViewDashboardAction())

            // Run all actions of a single virtual user for testing:
            // executeActions(Reader().actions)
        }
    }

}