package de.scandio.e4.testpackages.livelytheme

import de.scandio.e4.E4TestEnv
import de.scandio.e4.testpackages.TestPackageTestRun
import org.junit.Before
import org.junit.Test

class LivelyThemeTestRun : TestPackageTestRun() {

    private val TEST_PACKAGE = LivelyThemeTestPackage()

    @Before
    fun before() {
        // noop currently
    }

    @Test
    fun runTest() {
        if (E4TestEnv.PREPARATION_RUN) {
            executeTestPackagePrepare(TEST_PACKAGE)
        } else {
            executeTestPackage(TEST_PACKAGE)
        }
    }

}