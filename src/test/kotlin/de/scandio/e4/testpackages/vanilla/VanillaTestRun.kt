package de.scandio.e4.testpackages.vanilla

import de.scandio.e4.testpackages.TestPackageTestRun
import de.scandio.e4.testpackages.pagebranching.actions.CreateBranchAction
import de.scandio.e4.testpackages.pagebranching.actions.CreateOverviewPageAction
import de.scandio.e4.testpackages.pagebranching.actions.MergeBranchAction
import de.scandio.e4.testpackages.pagebranching.virtualusers.*
import de.scandio.e4.testpackages.vanilla.actions.*
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.TestPackage
import org.junit.Before
import org.junit.Test
import java.util.*

class VanillaTestRun : TestPackageTestRun() {

    private val BASE_URL = "http://confluence-cluster-6153-lb:26153/"
    private val OUT_DIR = "/tmp/e4/out"
    private val USERNAME = "admin"
    private val PASSWORD = "admin"
    private val TEST_PACKAGE = VanillaTestPackage()
    private val PREPARATION_RUN = false

    @Before
    fun before() {
        super.setup()
    }

    @Test
    fun runTest() {
        try {
            if (PREPARATION_RUN) {
                executeTestPackagePrepare(TEST_PACKAGE)
            } else {
//                executeTestPackage(TEST_PACKAGE)

                // Run a single action for testing:
                 executeAction(AddRandomCommentAction())

                // Run single virtual user for testing:
                // executeActions(BranchCreator().actions)
            }
        } finally {
            shot()
            super.shutdown()
        }
    }

    override fun getBaseUrl(): String { return BASE_URL }
    override fun getOutDir(): String { return OUT_DIR }
    override fun getUsername(): String { return USERNAME }
    override fun getPassword(): String { return PASSWORD }
    override fun getTestPackage(): TestPackage { return TEST_PACKAGE }

}