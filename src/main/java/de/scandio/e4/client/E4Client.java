package de.scandio.e4.client;

import de.scandio.e4.E4Application;
import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.client.config.ConfigUtil;
import org.apache.commons.cli.CommandLine;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

public class E4Client {
	private final CommandLine parsedArgs;

	public E4Client(CommandLine parsedArgs) {
		this.parsedArgs = parsedArgs;
	}

	public void start() {
		final String configPath = parsedArgs.getOptionValue("config");
		final ClientConfig clientConfig = ConfigUtil.readConfigFromFile(configPath);

		System.out.println();
		System.out.println(clientConfig);

		// TODO: validate config instead of just throwing exceptions in getters
		// TODO: test if we can connect to the target
		// TODO: test if pluginsToInstall can be found

		final List<String> workers = clientConfig.getWorkers();

		if (workers == null || workers.size() == 0) {
			System.out.println("No workers provided, starting a local worker...");
			startLocal(clientConfig);
		} else {
			System.out.println("Found workers, see if we can connect to workers.");
			// TODO: test if we can connect to workers
			// We are doing a remote run!
			//startRemote();
		}
	}

	private void startLocal(ClientConfig clientConfig) {
		final HashMap<String, Object> props = new HashMap<String, Object>(){{
			put("server.port", 0); // random port
		}};

		final ConfigurableApplicationContext run = new SpringApplicationBuilder()
				.sources(E4Application.class)
				.properties(props)
				.run();

		final String localWorkerURL = "http://localhost:" + run.getEnvironment().getProperty("local.server.port") + "/";
		final String localWorkerStatusURL = localWorkerURL + "e4/status";

		System.out.println("Started local worker at: " + localWorkerURL);
		System.out.println("Polling "+localWorkerStatusURL+" until it returns 200...");

		final RestTemplate restTemplate = new RestTemplate();
		boolean responseCodeIs200;
		try {
			do {
				Thread.sleep(1000);
				final ResponseEntity<String> response = restTemplate.getForEntity(localWorkerStatusURL, String.class);
				responseCodeIs200 = response.getStatusCodeValue() == 200;
				System.out.println("Polled endpoint, response code was: " + response.getStatusCodeValue());
			} while (!responseCodeIs200);

			System.out.println("Local worker is ready to go!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
