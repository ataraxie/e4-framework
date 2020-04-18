package de.scandio.e4.worker.rest;

import com.google.gson.Gson;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.services.StorageService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RestAtlassian implements RestClient {

	private static final Logger log = LoggerFactory.getLogger(RestAtlassian.class);

	private static final Gson GSON = new Gson();

	private Map<String, Long> contentIds = new HashMap<>();

	protected String username;
	protected String password;
	protected String baseUrl;

	@Nullable
	protected StorageService storageService;

	public RestAtlassian(StorageService storageService, String baseUrl, String username, String password) {
		this.storageService = storageService;
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}


	protected List<Map> getResultListFromResponse(String responseText) {
		Map<String, Object> response = GSON.fromJson(responseText, Map.class);
		List<Map> pageObjects = (ArrayList) response.get("results");
		return pageObjects;
	}

	protected String sendPutRequest(String urlAfterBaseUrl, String body) {
		return sendPostOrPutRequest(HttpMethod.PUT, urlAfterBaseUrl, body);
	}

	protected String sendPostRequest(String urlAfterBaseUrl, String body) {
		return sendPostOrPutRequest(HttpMethod.POST, urlAfterBaseUrl, body);
	}

	public int sendGetRequestReturnStatus(String urlAfterBaseUrl) {
		return sendGetRequestReturnResponse(urlAfterBaseUrl).getStatusCodeValue();
	}

	public String sendGetRequestReturnBody(String urlAfterBaseUrl) {
		return sendGetRequestReturnResponse(urlAfterBaseUrl).getBody();
	}

	protected ResponseEntity<String> sendGetRequestReturnResponse(String urlAfterBaseUrl) {
		return sendGetOrDeleteRequestReturnResponse(urlAfterBaseUrl, HttpMethod.GET);
	}

	protected int sendDeleteRequestReturnStatus(String urlAfterBaseUrl) {
		return sendGetOrDeleteRequestReturnResponse(urlAfterBaseUrl, HttpMethod.DELETE).getStatusCodeValue();
	}

	protected String sendPostOrPutRequest(HttpMethod method, String urlAfterBaseUrl, String body) {
		final String url = this.baseUrl + urlAfterBaseUrl;
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));

		log.debug("Sending {{}} request {{}} with body {{}}", method, url, body);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", getBasicAuth());
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

		HttpEntity<String> request = new HttpEntity<>(body, headers);

		ResponseEntity<String> response;
		try {
			response = restTemplate.exchange(url, method, request, String.class);
		} catch (HttpClientErrorException e) {
			log.error("Exception sending REST {{}} request for user {{}} with password {{}} and URL {{}}",
					method, this.username, this.password, url);
			throw e;
		}
		return response.getBody();
	}

	protected ResponseEntity<String> sendGetOrDeleteRequestReturnResponse(String urlAfterBaseUrl, HttpMethod method) {
		final String url = this.baseUrl + urlAfterBaseUrl;
		final RestTemplate restTemplate = new RestTemplateBuilder()
				.setConnectTimeout(Duration.ofSeconds(40))
				.setReadTimeout(Duration.ofSeconds(40)).build();

		log.debug("Sending {{}} request {{}}", method, url);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", getBasicAuth());

		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> response;
		try {
			response = restTemplate.exchange(url, method, request, String.class);
		} catch (HttpClientErrorException e) {
			log.error("Exception sending REST {{}} request for user {{}} with password {{}} and URL {{}}", method, this.username, this.password, url);
			throw e;
		}

		return response;
	}

	protected String getBasicAuth() {
		String plainCreds = this.username + ":" + this.password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		return "Basic " + base64Creds;
	}

	@Override
	public String getUser() {
		return this.username;
	}

}
