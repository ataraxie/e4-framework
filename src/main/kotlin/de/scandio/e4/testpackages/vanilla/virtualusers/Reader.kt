package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.CheckPageRestrictionsAction
import de.scandio.e4.testpackages.vanilla.actions.ViewBlogpostAction
import de.scandio.e4.testpackages.vanilla.actions.ViewPageInfoAction
import de.scandio.e4.testpackages.vanilla.actions.ViewPageAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser


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

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        val spaceKey = "E4"
        actions.add(ViewPageAction(spaceKey, "E4 Reader Page 1"))
        actions.add(ViewPageAction(spaceKey, "E4 Reader Page 2"))
        actions.add(ViewPageAction(spaceKey, "E4 Reader Page 3"))
        actions.add(ViewBlogpostAction(spaceKey, "E4 Reader Blogpost 1","2019/05/21"))
        actions.add(ViewBlogpostAction(spaceKey, "E4 Reader Blogpost 2","2019/05/21"))
        actions.add(CheckPageRestrictionsAction(spaceKey, "E4 Reader Page 1"))
        actions.add(ViewPageInfoAction(spaceKey, "E4 Reader Page 1"))
        return actions
    }
}