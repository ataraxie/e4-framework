package de.scandio.e4.testpackages.gitsnippets.actions

import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.testpackages.gitsnippets.GitSnippetsTestPackage
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.WorkerUtils
import org.slf4j.LoggerFactory
import java.util.*

open class SetupGitSnippetsMacroPagesAction (
        val spaceKey: String,
        val parentPageTitle: String = "",
        val howMany: Int = 1
    ) : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    protected var start: Long = 0
    protected var end: Long = 0

    override fun execute(webClient: WebClient, restClient: RestClient) {
        val restConfluence = restClient as RestConfluence
        val macroId = "live-snippet"
        this.start = Date().time
        try {
            repeat(howMany) {
                val pageTitle = "Setup Macro Page $macroId #$it (${Date().time})"
                val macroPageContent = createRandomStorageFormat(
                        GitSnippetsTestPackage.REPOSITORY_PATH,
                        WorkerUtils.getRandomItem(GitSnippetsTestPackage.FILE_PATHS),
                        GitSnippetsTestPackage.COMMIT_HASH)
                restConfluence.createPage(spaceKey, pageTitle, macroPageContent, parentPageTitle)
            }
        } catch (e: Exception) {
            log.warn("Failed creating page. Skipping.")
        }

        this.end = Date().time
    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }

    override fun isRestOnly(): Boolean {
        return true
    }

    fun createRandomStorageFormat(repoPath: String, filePath: String, commitHash: String) : String {
        return  """
            <ac:structured-macro ac:name="live-snippet" ac:schema-version="1" ac:macro-id="57db26e0-9dea-4c37-8817-04027cacdd95">
                <ac:parameter ac:name="bitbucketUrl">${repoPath}/${filePath}</ac:parameter>
                <ac:parameter ac:name="commitHash">${commitHash}</ac:parameter>
            </ac:structured-macro>
        """.trimIndent()
    }

}