package de.scandio.e4.testpackages.gitsnippets

import de.scandio.e4.testpackages.gitsnippets.actions.SetupGitSnippets
import de.scandio.e4.testpackages.gitsnippets.actions.SetupDiaryMacroPagesAction
import de.scandio.e4.testpackages.gitsnippets.actions.SetupGitSnippetsMacroPagesAction
import de.scandio.e4.testpackages.gitsnippets.virtualusers.DiaryEntryCreator
import de.scandio.e4.testpackages.gitsnippets.virtualusers.DiaryMacroPageReader
import de.scandio.e4.testpackages.gitsnippets.virtualusers.GitSnippetsMacroPageReader
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.CreateSpaceAction
import de.scandio.e4.testpackages.vanilla.virtualusers.*
import de.scandio.e4.worker.client.ApplicationName
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.TestPackage
import de.scandio.e4.worker.collections.VirtualUserCollection

/**
 * === GitSnippetsTestPackage ===
 *
 * Test package for app "Git Snippets for Confluence".
 *
 * Assumptions:
 * - Running Confluence
 * - App Git Snippets
 *
 * Setup:
 * - Create a space with key "GS" and name "Git Snippets"
 * - Configure Git Snippets in the Confluence Admin settings page (will add a Github auth token)
 * - Create a page with title "macros" in the "GS" space
 * - Create 100 pages with the Git Snippets macro and random macro parameters
 *
 * Virtual Users:
 * - GitSnippetsMacroPageReader (weight 0.2): views a random child page of the "macros" page (all of these contain
 *      one version of the Git Snippets macro
 *
 * Sum of weight is 0.2 which leaves 0.8 for vanilla virtual users.
 *
 * @author Felix Grund
 */
class GitSnippetsTestPackage: TestPackage {

    companion object {
        val COMMIT_HASH = "0681237c6c8cadab437c37ddc9764520fc6fc4b9"
        val REPOSITORY_PATH = "https://github.com/scandio/e4-framework/blob/master/"
        val FILE_PATHS = arrayListOf(
                "src/main/java/de/scandio/e4/E4Application.java",
                "src/main/java/de/scandio/e4/E4Env.java",
                "src/main/java/de/scandio/e4/worker/interfaces/RestClient.java",
                "src/main/java/de/scandio/e4/dto/ApplicationStatusResponse.java",
                "src/main/java/de/scandio/e4/dto/PreparationStatus.java",
                "src/main/java/de/scandio/e4/dto/TestsStatus.java",
                "src/main/java/de/scandio/e4/client/E4Client.java",
                "src/main/java/de/scandio/e4/client/WorkerRestUtil.java",
                "src/main/java/de/scandio/e4/worker/services/ApplicationStatusService.java",
                "src/main/java/de/scandio/e4/worker/services/PreparationService.java"
        )
    }

    override fun getSetupActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(CreateSpaceAction("GS", "Git Snippets", true))
        actions.add(SetupGitSnippets())
        actions.add(CreatePageAction("GS", "macros", "<ac:structured-macro ac:name=\"children\" />", true))
        actions.add(SetupGitSnippetsMacroPagesAction("GS", "macros", 100))
        return actions
    }

    override fun getVirtualUsers(): VirtualUserCollection {
        val virtualUsers = VirtualUserCollection()
        // 0.8
        virtualUsers.add(Commentor::class.java, 0.08)
        virtualUsers.add(Reader::class.java, 0.28)
        virtualUsers.add(Creator::class.java, 0.1)
        virtualUsers.add(Searcher::class.java, 0.12)
        virtualUsers.add(Editor::class.java, 0.1)
        virtualUsers.add(Dashboarder::class.java, 0.12)

        // 0.2
        virtualUsers.add(GitSnippetsMacroPageReader::class.java, 0.2)
        return virtualUsers
    }

    override fun getApplicationName(): ApplicationName {
        return ApplicationName.confluence
    }

}