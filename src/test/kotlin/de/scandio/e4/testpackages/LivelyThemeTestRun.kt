package de.scandio.e4.testpackages

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.livelytheme.LivelyThemeTestPackage
import de.scandio.e4.testpackages.livelytheme.virtualusers.*
import de.scandio.e4.worker.collections.VirtualUserCollection
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
        if (E4Env.PREPARATION_RUN) {
            executeTestPackagePrepare(TEST_PACKAGE)
        } else {
            executeActions(LivelyMacroPageReader().actions)
            executeActions(LivelyMacroPageCreator().actions)
            executeActions(LivelyThemeAdmin().actions)
            executeActions(LivelySpaceToggler().actions)
            executeActions(LivelyPageToggler().actions)
        }
    }

}