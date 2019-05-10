package de.scandio.e4.worker.confluence.rest;

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
import java.util.List;

public class RestConfluence implements RestClient {

	private static final Logger log = LoggerFactory.getLogger(RestConfluence.class);

	private String restBaseUrl;
	private String username;
	private String password;

	public RestConfluence(String baseUrl, String username, String password) {
		if (!baseUrl.endsWith("/")) {
			baseUrl += "/";
		}
		this.restBaseUrl = baseUrl + "rest/api/";
		this.username = username;
		this.password = password;
	}

	public String findPage(String spaceKey, String title) {
		String urlAfterBaseUrl = String.format("content?title=%s&spaceKey=%s", title, spaceKey);
		return sendGetRequest(urlAfterBaseUrl);
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
		return sendPostRequest("content/", body);
	}

	private String sendPostRequest(String urlAfterBaseUrl, String body) {
		final String url = this.restBaseUrl += urlAfterBaseUrl;
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

	private String sendGetRequest(String urlAfterBaseUrl) {
		final String url = this.restBaseUrl += urlAfterBaseUrl;
		final RestTemplate restTemplate = new RestTemplate();

		log.debug("Sending GET request {{}}", url);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", getBasicAuth());

		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		String responseText = response.getBody();

		log.debug("Response text {{}}", responseText);
		return responseText;
	}

	private String getBasicAuth() {
		String plainCreds = this.username + ":" + this.password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		return "Basic " + base64Creds;
	}

}
