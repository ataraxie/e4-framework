package de.scandio.e4.worker.rest;

import de.scandio.e4.worker.services.StorageService;
import de.scandio.e4.worker.util.WorkerUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestConfluence extends RestAtlassian {

	private static final Logger log = LoggerFactory.getLogger(RestConfluence.class);

	private static final int REST_ENTITY_SEARCH_LIMIT = 100;

	private Map<String, Long> contentIds = new HashMap<>();

	public RestConfluence(StorageService storageService, String baseUrl, String username, String password) {
		super(storageService, baseUrl, username, password);
	}

	private String findPage(String spaceKey, String title) {
		String urlAfterBaseUrl = String.format("rest/api/content?title=%s&spaceKey=%s", title, spaceKey);
		log.info("[REST] obtaining page data for spaceKey {{}} and title {{}}", spaceKey, title);
		return sendGetRequestReturnBody(urlAfterBaseUrl);
	}

	private List<Long> findChildPageIdsUseCache(String spaceKey, String parentPageTitle) {
		List<Long> ids = null;
		String cacheKey = this.username + ":" + spaceKey + ":" + parentPageTitle;
		if (this.storageService != null) {
			ids = this.storageService.getIdsByKey(cacheKey);
		}

		if (ids == null) {
			Long parentContentId = getContentIdUseCache(spaceKey, parentPageTitle);
			String restUrl = "rest/api/content/"+parentContentId+"/child/page";
			log.info("[REST] obtaining child page ids for user {{}}, spaceKey {{}}, parentPage {{}}", this.username, spaceKey, parentPageTitle);
			String responseText = sendGetRequestReturnBody(restUrl);
			ids = getContentIdsFromResponse(responseText);
			if (this.storageService != null) {
				this.storageService.setIdsForKey(cacheKey, ids);
			}
		}

		return ids;
	}

	private List<Long> findContentIdsUseCache(int limit, String type, String spaceKey) {
		List<Long> ids = null;
		String cacheKey = this.username + ":" + type + ":" + spaceKey + ":" + limit;
		if (this.storageService != null) {
			ids = this.storageService.getIdsByKey(cacheKey);
		}

		if (ids == null) {
			String url = "rest/api/content?start=0&limit=%s";
			if (type != null && spaceKey != null) {
				url = String.format(url + "&type=%s&spaceKey=%s", limit, type, spaceKey);
			} else if (spaceKey != null) {
				url = String.format(url + "&spaceKey=%s", limit, spaceKey);
			} else if (type != null) {
				url = String.format(url + "&type=%s", limit, type);
			} else {
				url = String.format(url, limit);
			}

			log.info("[REST] findContentIds for user {{}}, type {{}}, spaceKey {{}}", this.username, type, spaceKey);
			String body = sendGetRequestReturnBody(url);
			ids = getContentIdsFromResponse(body);
			if (this.storageService != null) {
				this.storageService.setIdsForKey(cacheKey, ids);
			}
		}

		return ids;
	}

	private List<Long> getContentIdsFromResponse(String body) {
		List<Long> ids = new ArrayList<>();
		List<Map> pageObjects = getResultListFromResponse(body);
		for (Map pageObj : pageObjects) {
			long id = Long.parseLong((String) pageObj.get("id"));
			ids.add(id);
		}
		return ids;
	}

	public void fillCachesIfEmpty() {
		findContentIdsUseCache(REST_ENTITY_SEARCH_LIMIT, null, null);
	}

	public Long getRandomContentId() {
		return getRandomContentId(null, null);
	}

	public Long getRandomContentId(String spaceKey, String parentPageTitle) {
		Long contentId;
		if (StringUtils.isBlank(parentPageTitle)) {
			contentId = WorkerUtils.getRandomItem(findContentIdsUseCache(REST_ENTITY_SEARCH_LIMIT, null, spaceKey));
		} else {
			contentId = WorkerUtils.getRandomItem(findChildPageIdsUseCache(spaceKey, parentPageTitle));
		}
		return contentId;
	}

	public List<String> getUsernames() {
		String body = sendGetRequestReturnBody("rest/api/group/confluence-users/member");
		return getListFromResponse(String.class, "username", body);
	}

	private <T> List<T> getListFromResponse(Class<T> clazz, String key, String responseText) {
		List<T> ret = new ArrayList<>();
		List<Map> maps = getResultListFromResponse(responseText);
		for (Map obj : maps) {
			T item = (T) obj.get(key);
			ret.add(item);
		}
		return ret;
	}

	public String createPage(String spaceKey, String pageTitle, String content) {
		return createPage(spaceKey, pageTitle, content, null);
	}

	public String createPage(String spaceKey, String pageTitle, String content, String parentPageTitle) {
		return createContentEntity("page", spaceKey, pageTitle, content, parentPageTitle);
	}

	public String createBlogpost(String spaceKey, String title, String content) {
		return createContentEntity("blogpost", spaceKey, title, content, null);
	}

	private String createContentEntity(String type, String spaceKey, String entityTitle, String content, String parentEntityTitle) {
		content = StringEscapeUtils.escapeJson(content);
		String bodyTemplate, body;
		if (StringUtils.isNotBlank(parentEntityTitle)) {
			long parentContentId = getContentIdUseCache(spaceKey, parentEntityTitle);
			bodyTemplate = "{\"type\":\"%s\",\"ancestors\":[{\"id\":%s}],\"title\":\"%s\",\"space\":{\"key\":\"%s\"},\"body\":{\"storage\":{\"value\":\"%s\",\"representation\":\"storage\"}}}";
			body = String.format(bodyTemplate, type, parentContentId, entityTitle, spaceKey, content);
		} else {
			bodyTemplate = "{\"type\":\"%s\",\"title\":\"%s\",\"space\":{\"key\":\"%s\"},\"body\":{\"storage\":{\"value\":\"%s\",\"representation\":\"storage\"}}}";
			body = String.format(bodyTemplate, type, entityTitle, spaceKey, content);
		}
		return sendPostRequest("rest/api/content/", body);
	}

	private Long getContentIdUseCache(String spaceKey, String pageTitle) {
		String cacheKey = spaceKey + ":" + pageTitle;
		Long pageId = contentIds.get(cacheKey);
		if (pageId == null) {
			String responseText = findPage(spaceKey, pageTitle);
			List<Map> pages = getResultListFromResponse(responseText);
			pageId = Long.parseLong((String) pages.get(0).get("id"));
			contentIds.put(spaceKey + ":" + pageTitle, pageId);
		}
		return pageId;
	}

	public String createSpace(String spaceKey, String spaceName) {
		String spaceDesc = "Space used for testing";
//		String bodyTemplate = "{\"key\":\"%s\",\"name\":\"%s\",\"description\":{\"plain\":{\"value\":\"%s\",\"representation\":\"plain\"}},\"metadata\":{}}";
		String bodyTemplate = "{\"key\":\"%s\",\"name\":\"%s\",\"description\":{\"plain\":{\"value\":\"%s\",\"representation\":\"plain\"}},\"metadata\":{}}";
		String body = String.format(bodyTemplate, spaceKey, spaceName, spaceDesc);
		return sendPostRequest("rest/api/space/", body);
	}

}
