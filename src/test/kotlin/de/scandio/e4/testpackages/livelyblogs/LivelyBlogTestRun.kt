package de.scandio.e4.testpackages.livelyblogs

import de.scandio.e4.E4TestEnv
import de.scandio.e4.testpackages.TestPackageTestRun
import org.junit.Before
import org.junit.Test

class LivelyBlogTestRun : TestPackageTestRun() {

    private val TEST_PACKAGE = LivelyBlogsTestPackage()

    @Before
    fun before() {
        // noop currently
    }

    @Test
    fun runTest() {
        if (E4TestEnv.PREPARATION_RUN) {
            executeTestPackagePrepare(TEST_PACKAGE)
        } else {
            executeTestPackage(TEST_PACKAGE, 1)

            // Run a single action for testing:
//            executeAction(CreatePageAction("LT", "macros", "<p>macro pages</p>", false))

            // Run single virtual user for testing:
//             executeActions(TEST_PACKAGE.getSystemSetupActions())
        }
    }

}