package de.scandio.e4.worker.rest;

import com.google.gson.Gson;
import de.scandio.e4.worker.interfaces.RestClient;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

	public RestAtlassian(String baseUrl, String username, String password) {
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

	protected String sendPostOrPutRequest(HttpMethod method, String urlAfterBaseUrl, String body) {
		final String url = this.baseUrl + urlAfterBaseUrl;
		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
//		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
//		interceptors.add(new LoggingRequestInterceptor());
//		restTemplate.setInterceptors(interceptors);

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

	public int sendGetRequestReturnStatus(String urlAfterBaseUrl) {
		return sendGetRequestReturnResponse(urlAfterBaseUrl).getStatusCodeValue();
	}

	public String sendGetRequestReturnBody(String urlAfterBaseUrl) {
		ResponseEntity<String> response = sendGetRequestReturnResponse(urlAfterBaseUrl);
		String responseText = response.getBody();
//		log.debug("Response text {{}}", responseText);
		return responseText;
	}

	protected ResponseEntity<String> sendGetRequestReturnResponse(String urlAfterBaseUrl) {
		final String url = this.baseUrl + urlAfterBaseUrl;
		final RestTemplate restTemplate = new RestTemplate();

		log.debug("Sending GET request {{}}", url);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", getBasicAuth());

		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<String> response;
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		} catch (HttpClientErrorException e) {
			log.error("Exception sending REST GET request for user {{}} with password {{}} and URL {{}}", this.username, this.password, url);
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
