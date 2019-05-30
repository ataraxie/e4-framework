package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.*
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.confluence.rest.RestConfluence
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.VirtualUser
import de.scandio.e4.worker.interfaces.WebClient


/**
 * Confluence Reader Action.
 *
 * Assumptions:
 * - Space with key "E4"
 * - 3 Pages with titles "E4 Reader Page 1", "E4 Reader Page 2", "E4 Reader Page 3"
 * - 2 Blogposts with creation date "2019/05/21" and titles
 *   "E4 Reader Blogpost 1", "E4 Reader Blogpost 2"
 *
 * Actions:
 * - Read page in space "E4" with title "E4 Reader Page 1" (SELENIUM)
 * - Read page in space "E4" with title "E4 Reader Page 2" (SELENIUM)
 * - Read page in space "E4" with title "E4 Reader Page 3" (SELENIUM)
 * - Read blogpost in space "E4" with title "E4 Reader Blogpost 1" and date "2019/05/21" (SELENIUM)
 * - Read blogpost in space "E4" with title "E4 Reader Blogpost 2" and date "2019/05/21" (SELENIUM)
 * - Check page restrictions on page in "E4" with title "E4 Reader Page 1" (SELENIUM)
 * - View page status of page in "E4" with title "E4 Reader Page 1" (SELENIUM)
 *
 * @author Felix Grund
 */
class Reader : VirtualUser {

    override fun getActions(webClient: WebClient, restClient: RestClient): ActionCollection {
        val actions = ActionCollection()
        actions.add(ViewRandomContent())
        return actions
    }
}