package de.scandio.e4.uitest.gitsnippets

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.helpers.DomHelper
import de.scandio.e4.testpackages.gitsnippets.GitSnippetsSeleniumHelper
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.openqa.selenium.support.ui.ExpectedConditions
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Generic Git Snippets UI Test Suite.
 *
 * Requirements:
 * - Running Confluence with Git Snippets installed
 *
 * Required environment variables:
 * - E4_APPLICATION_BASE_URL - Confluence base URL (e.g. http://localhost:8090)
 * - E4_OUT_DIR - absolute path to a directory where output should be written
 * - GIT_SNIPPETS_TOKEN - a token that can be used for public Github repositories (FIXME: rename var to GITHUB at some point, but be aware that this is also used elsewhere!)
 *
 * Optional (recommended) environment variables:
 * - E4_ENABLE_DUMPING - should be set to true so screenshots and HTML files are dumped to your E4_OUT_DIR
 *
 * PREPARATION:
 * - Create a temporary space
 * - Set the GIT_SNIPPETS_TOKEN in the Git Snippets Settings (FIXME: this could be moved to an actual test case at some point)
 *
 * TESTS:
 * (see individual tests)
 *
 * CLEANUP:
 * - Remove the temporary space
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitSnippetsTestSuite : BaseSeleniumTest() {
    lateinit var spaceKey: String
    lateinit var spaceName: String
    lateinit var restConfluence: RestConfluence
    lateinit var webConfluence: WebConfluence
    lateinit var helper: GitSnippetsSeleniumHelper
    lateinit var accessToken: String

    private val log = LoggerFactory.getLogger(javaClass)

    private val MACRO_ID = "live-snippet"
    private val GITHUB_URL_MARKDOWN = "https://github.com/livelyapps/pluploader/blob/master/README.md"
    private val GITHUB_URL_NO_MARKDOWN = "https://github.com/livelyapps/pluploader/blob/master/setup.py"

    @BeforeAll
    fun setup() {
        runWithDump {
            val timestamp = Date().time
            this.restConfluence = restClient() as RestConfluence
            this.webConfluence = webClient() as WebConfluence
            this.dom = this.webConfluence.dom
            this.helper = GitSnippetsSeleniumHelper(webConfluence, webConfluence.domHelper)
            this.spaceKey = "E4GS${timestamp}"
            this.spaceName = "E4 GS (${timestamp})"
            this.restConfluence.createSpace(this.spaceKey, this.spaceName)
            this.accessToken = helper.getAccessTokenFromEnvVar()
            this.webConfluence.login()
            this.helper.setAccessTokenInGitSnippetsConfig(this.accessToken)
        }
    }

    // BEGIN: Release/2.5.0
    @Test // GSCSRV-2 - Markdown Renderer disabled with markdown file
    fun testMarkdownRendererDisabledWithMarkdownFile() {
        runWithDump {
            val pageTitle = "Git Snippets Markdown Page (${Date().time})"
            webConfluence.login()
            helper.createGitSnippetsMacroPageWaitTillRendered(this.spaceKey, pageTitle, mapOf(
                    "bitbucketUrl" to GITHUB_URL_MARKDOWN
            ))
            dom.expectElementNotPresent(".git-snippet[data-render-markdown=\"true\"]")
            dom.expectElementNotPresent(".git-snippet[data-render-markdown=\"true\"] .markdown-container")
        }
    }

    @Test // GSCSRV-2 - Markdown Renderer enabled with markdown file
    fun testMarkdownRendererEnabledWithMarkdownFile() {
        runWithDump {
            val pageTitle = "Git Snippets Markdown Page (${Date().time})"
            webConfluence.login()
            helper.createGitSnippetsMacroPageWaitTillRendered(this.spaceKey, pageTitle, mapOf(
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
    fun testMarkdownRendererWarningShown() {
        runWithDump {
            val pageTitle = "Git Snippets Markdown Page (${Date().time})"
            webConfluence.login()
            helper.createGitSnippetsMacroPageKeepMacroBrowserOpen(this.spaceKey, pageTitle, mapOf(
                    "bitbucketUrl" to GITHUB_URL_MARKDOWN
            ))
            dom.awaitElementVisible("#macro-param-div-bitbucketUrl .aui-message-warning", 3)
        }
    }

    @Test // GSCSRV-15 - Warning for Markdown file is NOT shown if render markdown checkbox is checked
    fun testMarkdownRendererWarningNotShown() {
        runWithDump {
            val pageTitle = "Git Snippets Markdown Page (${Date().time})"
            webConfluence.login()
            helper.createGitSnippetsMacroPageKeepMacroBrowserOpen(this.spaceKey, pageTitle, mapOf(
                    "bitbucketUrl" to GITHUB_URL_MARKDOWN,
                    "renderMarkdown" to "true"
            ))
            dom.awaitSeconds(1)
            dom.expectElementNotPresent("#macro-param-div-bitbucketUrl .aui-message-warning")
        }
    }

    @Test // GSCSRV-8 Add and remove a bitbucket access key for a domain
    fun testAddAndRemoveBitbucketServer() {
        runWithDump {
            webConfluence.login()
            helper.goToGitSnippetsSettings()

            var listTokenUrlElements = dom.findElements("#bitbucket-server-access-token-list input[name=\"list-token-url\"]")
            var listTokenElements = dom.findElements("#bitbucket-server-access-token-list input[name=\"list-token\"]")
            assertEquals(1, listTokenUrlElements.size)
            assertEquals(1, listTokenElements.size)
            // We expect a empty list:
            var firstListTokenUrlElement = listTokenUrlElements[0]
            var firstListTokenElement = listTokenElements[0]
            assertEquals("", firstListTokenUrlElement.getAttribute("value"))
            assertEquals("", firstListTokenElement.getAttribute("value"))

            dom.insertText("#bitbucket-server-access-token-list .list-item:first-child input[name=\"list-token-url\"]",
                    "git.livelyapps.com", true)
            dom.insertText("#bitbucket-server-access-token-list .list-item:first-child input[name=\"list-token\"]",
                    "MASKED", true)

            dom.click("input.submit")

            dom.awaitElementPresent("#bitbucket-server-access-token-list input[name=\"list-token-url\"]")

            listTokenUrlElements = dom.findElements("#bitbucket-server-access-token-list input[name=\"list-token-url\"]")
            listTokenElements = dom.findElements("#bitbucket-server-access-token-list input[name=\"list-token\"]")
            assertEquals(1, listTokenUrlElements.size)
            assertEquals(1, listTokenElements.size)
            // We expect the data we just set, but masked
            firstListTokenUrlElement = listTokenUrlElements[0]
            firstListTokenElement = listTokenElements[0]
            assertEquals("git.livelyapps.com", firstListTokenUrlElement.getAttribute("value"))
            assertEquals(false, firstListTokenUrlElement.isEnabled)
            assertEquals("****************************************", firstListTokenElement.getAttribute("value"))

            dom.click("#bitbucket-server-access-token-list .list-item:first-child .aui-iconfont-cross")
            dom.click("input.submit")

            dom.awaitElementPresent("#bitbucket-server-access-token-list input[name=\"list-token-url\"]")

            listTokenUrlElements = dom.findElements("#bitbucket-server-access-token-list input[name=\"list-token-url\"]")
            listTokenElements = dom.findElements("#bitbucket-server-access-token-list input[name=\"list-token\"]")
            assertEquals(1, listTokenUrlElements.size)
            assertEquals(1, listTokenElements.size)
            // We expect a empty list:
            firstListTokenUrlElement = listTokenUrlElements[0]
            firstListTokenElement = listTokenElements[0]
            assertEquals("", firstListTokenUrlElement.getAttribute("value"))
            assertEquals("", firstListTokenElement.getAttribute("value"))
        }
    }
    // END: Release/2.6.0

    @AfterAll
    fun cleanup() {
        log.info("Removing space {{}} after test suite has executed", this.spaceKey)
        val status = this.restConfluence.removeSpace(this.spaceKey)
        log.info("Status code {{}}", status)
    }

}