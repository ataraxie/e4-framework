package de.scandio.e4.uitest.pagebranching

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.TestPackageTestRun
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsTestPackage
import de.scandio.e4.testpackages.livelyblogs.virtualusers.*
import de.scandio.e4.testpackages.pagebranching.PageBranchingTestPackage
import de.scandio.e4.testpackages.pagebranching.virtualusers.*
import org.junit.Before
import org.junit.Test

class PageBranchingSeleniumTestSuite : TestPackageTestRun() {

    @Test
    fun setupIfPreparationRun() { // FIXME: always run this test first!
        if (E4Env.PREPARATION_RUN) {
            executeTestPackagePrepare(PageBranchingTestPackage())
        }
    }
    @Test
    fun testBranchCreator() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(BranchCreator().actions)
        }
    }

    @Test
    fun testBranchMerger() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(BranchMerger().actions)
        }
    }

    @Test
    fun testBranchOverviewCreator() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(BranchOverviewCreator().actions)
        }
    }
    @Test
    fun testBranchOverviewReader() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(BranchOverviewReader().actions)
        }
    }
    @Test
    fun testBranchedPageReader() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(BranchedPageReader().actions)
        }
    }
    @Test
    fun testDiffViewer() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(DiffViewer().actions)
        }
    }

}