package de.scandio.e4.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.function.Predicate;

public class WorkerRestUtil {

	public static ResponseEntity<WorkerStatusResponse> getStatus(String workerUrl) {
		final RestTemplate restTemplate = new RestTemplate();
		final String workerStatusURL = workerUrl + "e4/status";
		return restTemplate.getForEntity(workerStatusURL, WorkerStatusResponse.class);
	}

	public static void pollStatusUntil(String workerUrl, int intervalMs, int maxPolls, Predicate<WorkerStatusResponse> predicate) throws Exception {
		ResponseEntity<WorkerStatusResponse> response;
		int polls = 0;

		do {
			Thread.sleep(intervalMs);
			response = getStatus(workerUrl);

			if (++polls == maxPolls) {
				throw new Exception("Exceeded polling limit. Aborting.");
			}
		} while (!predicate.test(response.getBody()));
	}

	public static ResponseEntity<String> postPrepare(String workerUrl, Map<String, Object> preparationParameters) {
		final RestTemplate restTemplate = new RestTemplate();
		final String workerPrepareURL = workerUrl + "e4/prepare";
		return restTemplate.postForEntity(workerPrepareURL, preparationParameters, String.class);
	}
}
