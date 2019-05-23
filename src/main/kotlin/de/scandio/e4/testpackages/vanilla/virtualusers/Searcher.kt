package de.scandio.e4.testpackages.vanilla.virtualusers

import de.scandio.e4.testpackages.vanilla.actions.QuicksearchAction
import de.scandio.e4.testpackages.vanilla.actions.SearchAction
import de.scandio.e4.testpackages.vanilla.actions.SearchAndClickFiltersAction
import de.scandio.e4.worker.collections.ActionCollection
import de.scandio.e4.worker.interfaces.VirtualUser


/**
 * Confluence Searcher VirtualUser.
 *
 * Assumptions:
 * - Space with key "E4"
 * - 3 Pages with titles "E4 Reader Page 1", "E4 Reader Page 2", "E4 Reader Page 3"
 *
 * Actions:
 * - Quicksearch for query "E4" (REST)
 * - Quicksearch for query "E4 Reader" (REST)
 * - Quicksearch for query "E4 Reader Page 1" (REST)
 * - Search for query "E4" on v3 search page (SELENIUM)
 * - Search for query "E4 Reader" on v3 search page (SELENIUM)
 * - Search for query "E4 Reader Page 1" on v3 search page (SELENIUM)
 * - Search for query "E4" on v3 search page and then click all filter links under
 *   "LAST MODIFIED" and "OF TYPE" (SELENIUM)
 *
 * @author Felix Grund
 */
class Searcher : VirtualUser {

    override fun getActions(): ActionCollection {
        val actions = ActionCollection()
        actions.add(QuicksearchAction("E4"))
        actions.add(QuicksearchAction("E4 Reader"))
        actions.add(QuicksearchAction("E4 Reader Page 1"))

        actions.add(SearchAction("E4"))
        actions.add(SearchAction("E4 Reader"))
        actions.add(SearchAction("E4 Reader Page 1"))

        actions.add(SearchAndClickFiltersAction("E4"))
        return actions
    }
}