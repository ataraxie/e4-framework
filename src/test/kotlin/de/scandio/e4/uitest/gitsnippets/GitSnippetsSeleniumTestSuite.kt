package de.scandio.e4.uitest.gitsnippets

import de.scandio.e4.E4Env
import de.scandio.e4.testpackages.TestPackageTestRun
import de.scandio.e4.testpackages.gitsnippets.GitSnippetsTestPackage
import de.scandio.e4.testpackages.gitsnippets.virtualusers.GitSnippetsMacroPageReader
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsTestPackage
import de.scandio.e4.testpackages.livelyblogs.virtualusers.*
import org.junit.Before
import org.junit.Test

class GitSnippetsSeleniumTestSuite : TestPackageTestRun() {

    @Test
    fun setupIfPreparationRun() { // FIXME: always run this test first!
        if (E4Env.PREPARATION_RUN) {
            executeTestPackagePrepare(GitSnippetsTestPackage())
        }
    }

    @Test
    fun testGitSnippetsMacroPageReader() {
        if (!E4Env.PREPARATION_RUN) {
            executeActions(GitSnippetsMacroPageReader().actions)
        }
    }

}