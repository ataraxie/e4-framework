package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.E4Env
import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.livelyblogs.actions.*
import de.scandio.e4.testpackages.vanilla.actions.CreateMultiplePagesActionRest
import de.scandio.e4.testpackages.vanilla.actions.CreatePageAction
import de.scandio.e4.testpackages.vanilla.actions.CreateSpaceAction
import de.scandio.e4.testpackages.vanilla.actions.ViewRandomContent
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.util.*
import kotlin.test.AfterTest


// REQUIRES:
// - Lively Blog installed
// - Confluence user admin/admin (if not configured differently with envvars)
//
// If you want the setup to run, set E4_PREPARATION_RUN envvar to true.
// FIXME: we should get rid of all references to the actions from the test packages that were used for data center testing.
//  instead, the logic from these actions should be moved into our LB Selenium helper.
class LivelyBlogSeleniumTestSuite : AbstractLivelyBlogTestSuite() {

    companion object {
        val spaceKey = if (E4Env.PREPARATION_RUN) "E4LB${Date().time}" else "LB"
        val spaceName = "E4 Lively Blog"
        val homePageTitle = "E4 Lively Blog Home"
        val macroParentPageTitle = "macros"

        @BeforeClass @JvmStatic internal fun beforeAll() {
            if (E4Env.PREPARATION_RUN) {
                runWithDump {
                    CreateSpaceAction(spaceKey, spaceName, true).execute(webConfluence, restConfluence)
                    CreateMultiplePagesActionRest(spaceKey, homePageTitle, 5).execute(webConfluence, restConfluence)
                    SetupSetSpaceForFeaturedPosts().execute(webConfluence, restConfluence)
                    SetupLivelyBlogCategories().execute(webConfluence, restConfluence)
                    UploadAllImages(spaceKey, homePageTitle, "random-image-1.jpg").execute(webConfluence, restConfluence)
                    CreatePageAction(spaceKey, macroParentPageTitle, "<p>macro pages</p>", true).execute(webConfluence, restConfluence)
                    SetupLivelyBlogMacroPagesAction(spaceKey, macroParentPageTitle, 5).execute(webConfluence, restConfluence)
                    SetupLivelyBlogPostsAction(spaceKey, homePageTitle, 5).execute(webConfluence, restConfluence)
                }
            }
        }
        @AfterClass @JvmStatic internal fun afterAll() {
            webClient.quit()
        }
    }

    @Test
    fun testLivelyBlogMacroPageCreator() {
        runWithDump {
            CreateRandomLivelyBlogMacroPage(spaceKey).execute(webConfluence, restConfluence)
        }
    }

    @Test
    fun testLivelyBlogMacroPageReader() {
        runWithDump {
            val randomContentId = restConfluence.getRandomContentId(spaceKey, macroParentPageTitle)
            webConfluence.login()
            webConfluence.goToPage(randomContentId)
            dom.awaitElementPresent(".lively-blog-posts", 5)
        }
    }

    @Test
    fun testLivelyBlogNavigator() {
        runWithDump {
            ViewRandomBlogpostOverview().execute(webConfluence, restConfluence)
        }
    }

    @Test
    fun testLivelyBlogSearcher() {
        runWithDump {
            SearchBlogpostOverview(spaceKey, spaceName).execute(webConfluence, restConfluence)
        }
    }
    @Test
    fun testLivelyBlogPostCreator() {
        runWithDump {
            CreateRandomLivelyBlogPost(spaceKey, homePageTitle).execute(webConfluence, restConfluence)
        }
    }
    @Test
    fun testLivelyBlogPageToBlogpostConvertor() {
        runWithDump {
            ConvertRandomPageToBlogPost(spaceKey).execute(webConfluence, restConfluence)
        }
    }

}