package de.scandio.e4.testpackages.pagebranching

import com.google.gson.Gson
import com.google.gson.JsonObject
import de.scandio.e4.clients.rest.RestConfluence

class PageBranchingRestHelper(
        protected val restConfluence: RestConfluence
) {

    fun createBranch(originPageId: Long, branchName: String): Long {
        val requestBody =
                """
{
  "branchName":"$branchName",
  "pageId":"$originPageId",
  "restrictToUser": "false"
}
"""
        val responseBody = restConfluence.sendPostRequest("rest/pagebranching/1.0/branchPage/", requestBody)
        val jsonObject = Gson().fromJson(responseBody, JsonObject::class.java)
        return jsonObject.get("branchPageId").asLong
    }

}