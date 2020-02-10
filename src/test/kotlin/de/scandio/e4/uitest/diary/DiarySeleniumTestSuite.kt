package de.scandio.e4.uitest.diary

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.TestPackageTestRun
import de.scandio.e4.testpackages.diary.DiaryTestPackage
import de.scandio.e4.testpackages.gitsnippets.virtualusers.DiaryEntryCreator
import org.junit.Test

class DiarySeleniumTestSuite : TestPackageTestRun() {

    @Test
    fun setupIfPreparationRun() {
        if (E4Env.PREPARATION_RUN) {
            executeTestPackagePrepare(DiaryTestPackage())
        }
    }

    @Test
    fun testDiaryEntryCreator() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(DiaryEntryCreator().actions)
            executeActions(DiaryEntryCreator().actions)
            executeActions(DiaryEntryCreator().actions)
            executeActions(DiaryEntryCreator().actions)
            executeActions(DiaryEntryCreator().actions)
            executeActions(DiaryEntryCreator().actions)
        }
    }

}