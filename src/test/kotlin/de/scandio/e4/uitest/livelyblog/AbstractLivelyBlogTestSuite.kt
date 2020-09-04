package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
open class AbstractLivelyBlogTestSuite : BaseSeleniumTest() {

    companion object {
        @JvmStatic
        var webConfluence = webClient as WebConfluence
        @JvmStatic
        var restConfluence = restClient as RestConfluence
        @JvmStatic
        var helper = LivelyBlogsSeleniumHelper(webConfluence)
    }

}