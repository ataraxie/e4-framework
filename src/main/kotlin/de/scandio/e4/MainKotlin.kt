package de.scandio.e4

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.testpackages.vanilla.scenarios.CreatePageScenario
import de.scandio.e4.testpackages.vanilla.scenarios.CreateSpaceScenario
import de.scandio.e4.worker.confluence.rest.RestConfluence
import org.openqa.selenium.WebDriver

class MainKotlin(
        val driver: WebDriver,
        val confluence: WebConfluence,
        val dom: DomHelper,
        val baseUrl: String
) {

    fun execute() {
        val restConfluence = RestConfluence(baseUrl, "admin", "admin")
        val spaceKey = "E48"
        CreateSpaceScenario(spaceKey, "E4 Home").execute(confluence, restConfluence)
        val pageTitle = "E4 Test Page"
        CreatePageScenario(spaceKey, pageTitle).execute(confluence, restConfluence)
    }

}