package de.scandio.e4.clients.rest

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import de.scandio.e4.worker.rest.RestAtlassian
import de.scandio.e4.worker.services.StorageService
import de.scandio.e4.worker.util.WorkerUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils
import org.slf4j.LoggerFactory

import java.util.ArrayList
import java.util.HashMap

@Suppress("UNCHECKED_CAST", "SameParameterValue")
class RestConfluence(
        storageService: StorageService?,
        baseUrl: String,
        username: String,
        password: String) : RestAtlassian(storageService, baseUrl, username, password) {

    private val log = LoggerFactory.getLogger(RestConfluence::class.java)

    private val GSON = Gson()

    private val contentIds = HashMap<String, Long>()
    private val allSpaceKeys: ArrayList<String> = arrayListOf()

    private val REST_ENTITY_SEARCH_LIMIT = 100


    private fun findPage(spaceKey: String?, title: String): String {
        val urlAfterBaseUrl = String.format("rest/api/content?title=%s&spaceKey=%s", title, spaceKey)
        log.info("[REST] obtaining page data for spaceKey {{}} and title {{}}", spaceKey, title)
        return sendGetRequestReturnBody(urlAfterBaseUrl)
    }

    private fun findChildPageIdsUseCache(spaceKey: String?, parentPageTitle: String): List<Long> {
        var ids: List<Long>? = null
        val cacheKey = this.username + ":" + spaceKey + ":" + parentPageTitle
        if (this.storageService != null) {
            ids = this.storageService!!.getIdsByKey(cacheKey)
        }

        if (ids == null) {
            val parentContentId = getContentIdUseCache(spaceKey, parentPageTitle)
            val restUrl = "rest/api/content/$parentContentId/child/page"
            log.info("[REST] obtaining child page ids for user {{}}, spaceKey {{}}, parentPage {{}}", this.username, spaceKey, parentPageTitle)
            val responseText = sendGetRequestReturnBody(restUrl)
            ids = getContentIdsFromResultsResponse(responseText)
            if (this.storageService != null) {
                this.storageService!!.setIdsForKey(cacheKey, ids)
            }
        }

        return ids
    }

    private fun findContentIdsUseCache(limit: Int, type: String?, spaceKey: String?): List<Long> {
        var ids: List<Long>? = null
        val cacheKey = this.username + ":" + type + ":" + spaceKey + ":" + limit
        if (this.storageService != null) {
            ids = this.storageService!!.getIdsByKey(cacheKey)
        }

        if (ids == null) {
            var url = "rest/api/content?start=0&limit=%s"
            if (type != null && spaceKey != null) {
                url = String.format("$url&type=%s&spaceKey=%s", limit, type, spaceKey)
            } else if (spaceKey != null) {
                url = String.format("$url&spaceKey=%s", limit, spaceKey)
            } else if (type != null) {
                url = String.format("$url&type=%s", limit, type)
            } else {
                url = String.format(url, limit)
            }

            log.info("[REST] findContentIds for user {{}}, type {{}}, spaceKey {{}}", this.username, type, spaceKey)
            val body = sendGetRequestReturnBody(url)
            ids = getContentIdsFromResultsResponse(body)
            if (this.storageService != null) {
                this.storageService!!.setIdsForKey(cacheKey, ids)
            }
        }

        return ids
    }

    private fun getContentIdsFromResultsResponse(body: String): List<Long> {
        val ids = ArrayList<Long>()
        val pageObjects = getResultListFromResponse(body)
        for (pageObj in pageObjects) {
            val id = java.lang.Long.parseLong(pageObj["id"] as String)
            ids.add(id)
        }
        return ids
    }

    private fun getContentIdFromCreateResponse(body: String): Long {
        val jsonObject = GSON.fromJson(body, JsonObject::class.java)
        return jsonObject.get("id").asLong
    }

    fun fillCachesIfEmpty() {
        findContentIdsUseCache(REST_ENTITY_SEARCH_LIMIT, null, null)
        findSpaceKeysUseCache()
    }

    fun getRandomContentId(spaceKey: String? = null, parentPageTitle: String? = null): Long {
        val contentId: Long
        if (StringUtils.isBlank(parentPageTitle)) {
            contentId = WorkerUtils.getRandomItem(findContentIdsUseCache(REST_ENTITY_SEARCH_LIMIT, null, spaceKey))
        } else {
            contentId = WorkerUtils.getRandomItem(findChildPageIdsUseCache(spaceKey, parentPageTitle!!))
        }
        return contentId
    }

    override fun getUsernames(): List<String> {
        val body = sendGetRequestReturnBody("rest/api/group/confluence-users/member")
        return getListFromResponse("username", body)
    }

    private fun <T> getListFromResponse(key: String, responseText: String): List<T> {
        val ret = ArrayList<T>()
        val maps = getResultListFromResponse(responseText)
        for (obj in maps) {
            val item = obj[key] as T
            ret.add(item)
        }
        return ret
    }

    fun createPage(spaceKey: String, pageTitle: String, content: String, parentPageTitle: String? = null): Long {
        return createContentEntity("page", spaceKey, pageTitle, content, parentPageTitle)
    }

    fun createBlogpost(spaceKey: String, title: String, content: String): Long {
        return createContentEntity("blogpost", spaceKey, title, content, null)
    }

    private fun createContentEntity(
            type: String,
            spaceKey: String,
            entityTitle: String,
            unescapedContent: String,
            parentEntityTitle: String?
    ): Long {

        val content = StringEscapeUtils.escapeJson(unescapedContent)
        val bodyObj = jsonObject("""
{
  "type":"$type",
  "title":"$entityTitle",
  "space":{
    "key":"$spaceKey"
  },
  "body":{
    "storage":{
      "value":"$content",
      "representation":"storage"
    }
  }
}""")
        if (StringUtils.isNotBlank(parentEntityTitle)) {
            val parentContentId = getContentIdUseCache(spaceKey, parentEntityTitle!!)!!
            val ancestorsObj = jsonElement("""[{"id":$parentContentId}]""")
            bodyObj.add("ancestors", ancestorsObj)
        }
//        if (labels.isNotEmpty()) {
//            val metadataObj = JsonObject()
//            val labelArr = JsonArray()
//            for (label in labels) {
//                val labelObj = JsonObject()
//                labelObj.addProperty("name", label)
//                labelArr.add(labelObj)
//            }
//            metadataObj.add("labels", labelArr)
//            bodyObj.add("metadata", metadataObj)
//        }

        val responseText = sendPostRequest("rest/api/content/", GSON.toJson(bodyObj))
        return getContentIdFromCreateResponse(responseText)
    }

    fun getContentIdUseCache(spaceKey: String?, pageTitle: String): Long? {
        val cacheKey = "$spaceKey:$pageTitle"
        var pageId: Long? = contentIds[cacheKey]
        if (pageId == null) {
            val responseText = findPage(spaceKey, pageTitle)
            val pages = getResultListFromResponse(responseText)
            pageId = java.lang.Long.parseLong(pages[0]["id"] as String)
            contentIds["$spaceKey:$pageTitle"] = pageId
        }
        return pageId
    }

    fun findSpaceKeysUseCache(): List<String> {
        if (this.allSpaceKeys.isEmpty()) {
            this.allSpaceKeys.addAll(retrieveAllSpaceKeys())
        }
        return this.allSpaceKeys
    }

    fun createSpace(spaceKey: String, spaceName: String): String {
        val spaceDesc = "Space used for testing"
        val body =
"""
{
  "key":"$spaceKey",
  "name":"$spaceName",
  "description": {
    "plain": {
      "value":"$spaceDesc",
      "representation":"plain"
    }
  },
  "metadata": {}
}
"""
        return sendPostRequest("rest/api/space/", body)
    }

    fun jsonObject(jsonString: String): JsonObject {
        return GSON.fromJson(jsonString, JsonObject::class.java)
    }

    fun jsonElement(jsonString: String): JsonElement {
        return GSON.fromJson(jsonString, JsonElement::class.java)
    }

    fun addLabelsToContentEntity(contentId: Long, labels: List<String>) {
        val labelArr = JsonArray()
        for (label in labels) {
            val labelObj = JsonObject()
            labelObj.addProperty("name", label)
            labelArr.add(labelObj)
        }
        val body = GSON.toJson(labelArr)
        sendPostRequest("rest/api/content/$contentId/label", body)
    }

    fun spaceExists(spaceKey: String): Boolean {
        return try {
            val response = sendGetRequestReturnResponse("rest/api/space?spaceKey=$spaceKey")
            response.statusCodeValue == 200 && !response.body.contains("\"results\":[]")
        } catch (e: Exception) {
            log.warn("Error sending get request for spaceExists")
            false
        }
    }

    private fun retrieveAllSpaceKeys(): List<String> {
        val response = sendGetRequestReturnResponse("rest/api/space")
        return getListFromResponse("key", response.body)
    }

}
