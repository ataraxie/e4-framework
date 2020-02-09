package de.scandio.e4.testpackages

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsTestPackage
import de.scandio.e4.testpackages.livelyblogs.virtualusers.*
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
        if (E4Env.PREPARATION_RUN) {
            executeTestPackagePrepare(TEST_PACKAGE)
        } else {
            executeActions(LivelyBlogMacroPageCreator().actions)
            executeActions(LivelyBlogMacroPageReader().actions)
            executeActions(LivelyBlogNavigator().actions)
            executeActions(LivelyBlogSearcher().actions)
            executeActions(LivelyBlogPostCreator().actions)
            executeActions(LivelyBlogPageToBlogpostConvertor().actions)
        }
    }

}