package de.scandio.e4.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class WorkerStatusUtil {

	public static ResponseEntity<String> getStatus(String workerUrl) {
		final RestTemplate restTemplate = new RestTemplate();
		final String workerStatusURL = workerUrl + "e4/status";
		return restTemplate.getForEntity(workerStatusURL, String.class);
	}
}
