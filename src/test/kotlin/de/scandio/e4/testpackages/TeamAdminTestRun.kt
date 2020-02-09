package de.scandio.e4.testpackages

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.diary.TeamAdminTestPackage
import de.scandio.e4.testpackages.gitsnippets.virtualusers.DiaryEntryCreator
import de.scandio.e4.testpackages.gitsnippets.virtualusers.SpaceGroupCreator
import org.junit.Before
import org.junit.Test

class TeamAdminTestRun : TestPackageTestRun() {

    @Before
    fun before() {
        // noop currently
    }

    @Test
    fun runTest() {
        if (E4Env.PREPARATION_RUN) {
            print("PREPARATION_RUN was set but no prepare actions are present")
        } else {
            executeActions(SpaceGroupCreator().actions)
        }
    }

}