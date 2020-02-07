package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.TestPackageTestRun
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsTestPackage
import de.scandio.e4.testpackages.livelyblogs.virtualusers.*
import org.junit.Before
import org.junit.Test

class LivelyBlogSeleniumTestSuite : TestPackageTestRun() {

    @Test
    fun setupIfPreparationRun() { // FIXME: always run this test first!
        if (E4Env.PREPARATION_RUN) {
            executeTestPackagePrepare(LivelyBlogsTestPackage())
        }
    }

    @Test
    fun testLivelyBlogMacroPageCreator() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(LivelyBlogMacroPageCreator().actions)
        }
    }

    @Test
    fun testLivelyBlogMacroPageReader() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(LivelyBlogMacroPageReader().actions)
        }
    }

    @Test
    fun testLivelyBlogNavigator() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(LivelyBlogNavigator().actions)
        }
    }
    @Test
    fun testLivelyBlogSearcher() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(LivelyBlogSearcher().actions)
        }
    }
    @Test
    fun testLivelyBlogPostCreator() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(LivelyBlogPostCreator().actions)
        }
    }
    @Test
    fun testLivelyBlogPageToBlogpostConvertor() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(LivelyBlogPageToBlogpostConvertor().actions)
        }
    }

}