package de.scandio.e4.testpackages.pagebranching

import de.scandio.e4.testpackages.TestPackageTestRun
import de.scandio.e4.testpackages.pagebranching.actions.CreateBranchAction
import de.scandio.e4.testpackages.pagebranching.actions.CreateOverviewPageAction
import de.scandio.e4.testpackages.pagebranching.actions.MergeBranchAction
import de.scandio.e4.testpackages.pagebranching.virtualusers.*
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.ViewPageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.TestPackage
import org.junit.Before
import org.junit.Test
import java.util.*

class PageBranchingTestRun : TestPackageTestRun() {

    private val BASE_URL = "http://contabo:8090/"
    private val OUT_DIR = "/tmp/e4/out"
    private val USERNAME = "admin"
    private val PASSWORD = "admin"
    private val TEST_PACKAGE = PageBranchingTestPackage()

    @Before
    fun before() {
        super.setup()
    }

    @Test
    fun runTest() {
        try {
//            actions.add(CreatePageAction("PB", "PB Origin 1", timestamp))
//            actions.addAll(OriginPageReader().actions)
//            actions.addAll(BranchedPageReader().actions)
//            actions.add(CreateBranchAction("PB", "BranchCreator Origin Manual", "Branch 1"))
//            actions.add(MergeBranchAction("PB", "BranchCreator Origin Manual", "Branch 1"))
//            actions.add(ViewPageAction("PB", "BranchedPageReader Origin"))
//            actions.add(CreateOverviewPageAction("PB", "PageReader Origin"))
            val measurement = executeActions(BranchOverviewCreator().actions)
            print("Total time taken: ${measurement.totalTimeTaken} (ran actions: ${measurement.numActionsRun}; excluded actions: ${measurement.numExcludedActions})")
        } finally {
            webConfluence!!.driver.quit()
        }
    }

    override fun getBaseUrl(): String { return BASE_URL }
    override fun getOutDir(): String { return OUT_DIR }
    override fun getUsername(): String { return USERNAME }
    override fun getPassword(): String { return PASSWORD }
    override fun getTestPackage(): TestPackage { return TEST_PACKAGE }

}