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
import org.junit.Test
import java.util.*


// REQUIRES:
// - Lively Blog installed
// - Confluence user admin/admin (if not configured differently with envvars)
//
// If you want the setup to run, set E4_PREPARATION_RUN envvar to true.
class LivelyBlogSeleniumTestSuite : BaseSeleniumTest() {

    val spaceKey = if (E4Env.PREPARATION_RUN) "LB${Date().time}" else "LB"
    val spaceName = "E4 Lively Blog"
    val homePageTitle = "E4 Lively Blog Home"
    val macroParentPageTitle = "macros"
    val restConfluence = restClient() as RestConfluence
    val webConfluence = webClient() as WebConfluence

    init {
        if (E4Env.PREPARATION_RUN) {
            runWithDump {
                CreateSpaceAction(spaceKey, spaceName, true).execute(webConfluence, restConfluence)
                CreateMultiplePagesActionRest(spaceKey, homePageTitle, 10).execute(webConfluence, restConfluence)
                SetupSetSpaceForFeaturedPosts().execute(webConfluence, restConfluence)
                SetupLivelyBlogCategories().execute(webConfluence, restConfluence)
                UploadAllImages(spaceKey, homePageTitle, "random-image-[1-3]\\.jpg").execute(webConfluence, restConfluence)
                CreatePageAction(spaceKey, macroParentPageTitle, "<p>macro pages</p>", true).execute(webConfluence, restConfluence)
                SetupLivelyBlogMacroPagesAction(spaceKey, macroParentPageTitle, 10).execute(webConfluence, restConfluence)
                SetupLivelyBlogPostsAction(spaceKey, homePageTitle, 10).execute(webConfluence, restConfluence)
            }
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
            ViewRandomContent(spaceKey, macroParentPageTitle, ".lively-blog-posts").execute(webConfluence, restConfluence)
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