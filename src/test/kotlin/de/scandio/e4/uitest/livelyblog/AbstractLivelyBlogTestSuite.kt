package de.scandio.e4.uitest.livelyblog

import de.scandio.e4.adhoc.BaseSeleniumTest
import de.scandio.e4.clients.rest.RestConfluence
import de.scandio.e4.clients.web.WebConfluence
import de.scandio.e4.testpackages.livelyblogs.LivelyBlogsSeleniumHelper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS)
open class AbstractLivelyBlogTestSuite : BaseSeleniumTest() {

    protected val webConfluence = webClient as WebConfluence
    protected val restConfluence = restClient as RestConfluence
    protected val helper = LivelyBlogsSeleniumHelper(webConfluence)

}