package de.scandio.e4.uitest.gitsnippets

import de.scandio.e4.E4Env
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.util.StringUtils
import org.junit.runners.MethodSorters
import java.util.*
import kotlin.test.assertEquals

// REQUIRES:
// - PageBranching installed
// - Confluence user admin/admin (if not configured differently with envvars)
//
// If you want the setup to run, set E4_PREPARATION_RUN envvar to true.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GitSnippetsSeleniumTestSuite : AbstractGitSnippetsTestSuite() {

    private val MACRO_ID = "live-snippet"
    private val GITHUB_URL_MARKDOWN = "https://github.com/livelyapps/pluploader/blob/master/README.md"
    private val GITHUB_URL_NO_MARKDOWN = "https://github.com/livelyapps/pluploader/blob/master/setup.py"

    companion object {
        @BeforeClass
        @JvmStatic internal fun beforeAll() {
            webConfluence.login()
            if (E4Env.PREPARATION_RUN) {
                try {
                    restConfluence.createSpace(SPACEKEY, SPACENAME)
                } catch (err: Exception) {
                    log.warn("Space $SPACEKEY could not be created. It might exist already.")
                }
                runWithDump {
                    webHelper.setGithubAccessTokenInAdmin(webHelper.getGithubAccessTokenFromEnvVar())
                }
            }
        }
    }

    // BEGIN: Release/2.5.0
    @Test // GSCSRV-2 - Markdown Renderer disabled with markdown file
    fun test250MarkdownRendererDisabledWithMarkdownFile() {
        runWithDump {
            val pageTitle = "Git Snippets Markdown Page"
            webHelper.createGitSnippetsMacroPageWaitTillRendered(SPACEKEY, pageTitle, mapOf(
                    "bitbucketUrl" to GITHUB_URL_MARKDOWN
            ))
            dom.expectElementNotPresent(".git-snippet[data-render-markdown=\"true\"]")
            dom.expectElementNotPresent(".git-snippet[data-render-markdown=\"true\"] .markdown-container")
        }
    }

    @Test // GSCSRV-2 - Markdown Renderer enabled with markdown file
    fun test250MarkdownRendererEnabledWithMarkdownFile() {
        runWithDump {
            val pageTitle = "Git Snippets Markdown Page"
            webHelper.createGitSnippetsMacroPageWaitTillRendered(SPACEKEY, pageTitle, mapOf(
                    "bitbucketUrl" to GITHUB_URL_MARKDOWN,
                    "renderMarkdown" to "true"
            ))
            dom.expectElementPresent(".git-snippet[data-render-markdown=\"true\"]")
            dom.expectElementPresent(".git-snippet[data-render-markdown=\"true\"] .markdown-container")
        }
    }
    // END: Release/2.5.0

    // BEGIN: Release/2.6.0
    @Test // GSCSRV-15 - Warning for Markdown file shown if render markdown checkbox is not checked
    fun test260MarkdownRendererWarningShown() {
        runWithDump {
            val pageTitle = "Git Snippets Markdown Page"
            webHelper.createGitSnippetsMacroPageKeepMacroBrowserOpen(SPACEKEY, pageTitle, mapOf(
                    "bitbucketUrl" to GITHUB_URL_MARKDOWN
            ))
            dom.awaitElementVisible("#macro-param-div-bitbucketUrl .aui-message-warning", 3)
        }
    }

    @Test // GSCSRV-15 - Warning for Markdown file is NOT shown if render markdown checkbox is checked
    fun test260MarkdownRendererWarningNotShown() {
        runWithDump {
            val pageTitle = "Git Snippets Markdown Page"
            webHelper.createGitSnippetsMacroPageKeepMacroBrowserOpen(SPACEKEY, pageTitle, mapOf(
                    "bitbucketUrl" to GITHUB_URL_MARKDOWN,
                    "renderMarkdown" to "true"
            ))
            dom.awaitSeconds(1)
            dom.expectElementNotPresent("#macro-param-div-bitbucketUrl .aui-message-warning")
        }
    }

    @Test // GSCSRV-8 Add and remove a bitbucket access key for a domain
    fun test260AddAndRemoveBitbucketServer() {

        fun save() {
            dom.click("#bitbucket-server-settings .aui-button.git-snippets-save-sources")
            webConfluence.awaitSuccessFlag()
        }

        fun addTokenAndSave(url: String, token: String) {
            dom.insertText("#new-bb-url-input", url)
            dom.insertText("#new-bb-token-input", token)
            dom.click("#add-new-token")
            dom.awaitMilliseconds(200)
            save()
            dom.awaitSeconds(1)
        }

        fun expectLastTokenToMatch(sampleUrl: String, sampleToken: String = "") {
            val listTokenUrlElements = dom.findElements("#bb-server-token-auth .token-row .url")
            val listTokenElements = dom.findElements("#bb-server-token-auth .token-row .access-token")
            val lastListTokenUrlElement = listTokenUrlElements[listTokenUrlElements.size-1]
            val lastListTokenElement = listTokenElements[listTokenElements.size-1]
            assertEquals(sampleUrl, lastListTokenUrlElement.text)
            if (StringUtils.isNotBlank(sampleToken)) {
                assertEquals(sampleToken, lastListTokenElement.getAttribute("data-token-raw"))
            }
        }

        fun removeFirstTokenAndSave() {
            dom.click("#bb-server-token-auth .token-row .aui-iconfont-cross")
            save()
            dom.awaitSeconds(1)
        }

        fun expectNumTokens(num: Int) {
            assertEquals(num, dom.findElements("#bb-server-token-auth .token-row").size)
        }

        fun goToBitbucketServerAccessTokens() {
            webHelper.goToGitSnippetsSettings("bitbucket-server", "access-tokens")
        }

        runWithDump {
            goToBitbucketServerAccessTokens()
            expectNumTokens(0)
            val sampleUrl1 = "https://sample-url-${Date().time}"
            val sampleToken1 = "sample-token-${Date().time}"
            addTokenAndSave(sampleUrl1, sampleToken1)
            expectNumTokens(1)
            expectLastTokenToMatch(sampleUrl1, sampleToken1)
            val sampleUrl2 = "https://sample-url-${Date().time}"
            val sampleToken2 = "sample-token-${Date().time}"
            addTokenAndSave(sampleUrl2, sampleToken2)
            expectNumTokens(2)
            expectLastTokenToMatch(sampleUrl2, sampleToken2)
            expectNumTokens(2)
            goToBitbucketServerAccessTokens()
            expectLastTokenToMatch(sampleUrl2)
            expectNumTokens(2)
            removeFirstTokenAndSave()
            expectNumTokens(1)
            expectLastTokenToMatch(sampleUrl2)
            expectNumTokens(1)
            removeFirstTokenAndSave()
            expectNumTokens(0)
        }
    }

    // END: Release/2.6.0

}