package de.scandio.e4.worker.confluence.rest;

import com.google.gson.Gson;
import de.scandio.e4.worker.interfaces.RestClient;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestConfluence implements RestClient {

	private static final Logger log = LoggerFactory.getLogger(RestConfluence.class);

	private static final Gson GSON = new Gson();

	private String username;
	private String password;
	private String baseUrl;

	public RestConfluence(String baseUrl, String username, String password) {
		this.baseUrl = baseUrl;
		if (!baseUrl.endsWith("/")) {
			this.baseUrl += "/";
		}
		this.username = username;
		this.password = password;
	}

	public String findPage(String spaceKey, String title) {
		String urlAfterBaseUrl = String.format("rest/api/content?title=%s&spaceKey=%s", title, spaceKey);
		return sendGetRequestReturnBody(urlAfterBaseUrl);
	}

	public List<Long> findPages(int limit) {
		List<Long> pageIds = new ArrayList<>();
		String url = String.format("/rest/api/content?type=page&start=0&limit=%s", limit);
		String body = sendGetRequestReturnBody(url);
		Map<String, Object> response = GSON.fromJson(body, Map.class);
		List<Map> pageObjects = (ArrayList) response.get("results");
		for (Map pageObj : pageObjects) {
			long pageId = Long.parseLong((String) pageObj.get("id"));
			pageIds.add(pageId);
		}
		return pageIds;
	}

	public String createPage(String pageTitle, String spaceKey, String content, String parentPageId) {
		String bodyTemplate = "{\"type\":\"page\",\"ancestors\":[{\"id\":%s}]\"title\":\"%s\",\"space\":{\"key\":\"%s\"},\"body\":{\"storage\":{\"value\":\"%s\",\"representation\":\"storage\"}}}";
		String body = String.format(bodyTemplate, parentPageId, pageTitle, spaceKey, content);
		return sendPostRequest("content/", body);
	}

	public String createSpace(String spaceKey, String spaceName) {
		String spaceDesc = "E4 created space. Enjoy!";
//		String bodyTemplate = "{\"key\":\"%s\",\"name\":\"%s\",\"description\":{\"plain\":{\"value\":\"%s\",\"representation\":\"plain\"}},\"metadata\":{}}";
		String bodyTemplate = "{\"key\":\"TST\",\"name\":\"Example space\",\"description\":{\"plain\":{\"value\":\"This is an example space\",\"representation\":\"plain\"}},\"metadata\":{}}";
		String body = String.format(bodyTemplate, spaceKey, spaceName, spaceDesc);
		return sendPostRequest("rest/api/content/", body);
	}

	private String sendPostRequest(String urlAfterBaseUrl, String body) {
		final String url = this.baseUrl + urlAfterBaseUrl;
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new LoggingRequestInterceptor());
		restTemplate.setInterceptors(interceptors);

		log.debug("Sending POST request {{}} with body {{}}", url, body);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", getBasicAuth());
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<String> request = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		String responseText = response.getBody();
		log.debug("Response text {{}}", responseText);
		return responseText;
	}

	public int sendGetRequestReturnStatus(String urlAfterBaseUrl) {
		return sendGetRequestReturnResponse(urlAfterBaseUrl).getStatusCodeValue();
	}

	public String sendGetRequestReturnBody(String urlAfterBaseUrl) {
		ResponseEntity<String> response = sendGetRequestReturnResponse(urlAfterBaseUrl);
		String responseText = response.getBody();
		log.debug("Response text {{}}", responseText);
		return responseText;
	}

	private ResponseEntity<String> sendGetRequestReturnResponse(String urlAfterBaseUrl) {
		final String url = this.baseUrl += urlAfterBaseUrl;
		final RestTemplate restTemplate = new RestTemplate();

		log.debug("Sending GET request {{}}", url);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", getBasicAuth());

		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		return response;
	}

	private String getBasicAuth() {
		String plainCreds = this.username + ":" + this.password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		return "Basic " + base64Creds;
	}

}
