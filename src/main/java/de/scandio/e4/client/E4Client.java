package de.scandio.e4.client;

import de.scandio.e4.E4Application;
import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.client.config.ConfigUtil;
import org.apache.commons.cli.CommandLine;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

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
		System.out.println();

		// TODO: validate config instead of just throwing exceptions in getters
		// TODO: test if we can connect to the target
		// TODO: test if pluginsToInstall can be found

		final List<String> workers = clientConfig.getWorkers();

		if (workers == null || workers.size() == 0) {
			System.out.println("No workers provided, starting a local worker...");
			startLocal(clientConfig);
		} else {
			System.out.println("Found remote workers! Let's see if we can connect to them.");

			for (String workerURL: workers) {
				try {
					final int statusCode = WorkerStatusUtil.getStatus(workerURL).getStatusCodeValue();
					if (statusCode == 200) {
						System.out.println(workerURL + "e4/status returned 200 - OK!");
					} else {
						throw new Exception("Status code wasn't 200 but " + statusCode);
					}
				} catch (Exception ex) {
					System.out.println("Worker unavailable: "+workerURL);
					System.out.println(ex.getMessage());
					System.exit(1);
				}
			}

			//startRemote();
			System.out.println("TODO: implement orchestration of remote workers");
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

		final String localWorkerPort = run.getEnvironment().getProperty("local.server.port");
		final String localWorkerURL = "http://localhost:" + localWorkerPort + "/";

		System.out.println("Started local worker at: " + localWorkerURL);
		System.out.println("Checking if local worker is healthy via: "+localWorkerURL+"e4/status");

		final int statusCode = WorkerStatusUtil.getStatus(localWorkerURL).getStatusCodeValue();

		if (statusCode == 200) {
			System.out.println("Local worker is healthy and enjoying itself!");
		} else {
			System.out.println("Local worker is unhealthy. Status code was: " + statusCode);
			System.out.println("Aborting...");
			System.exit(1);
		}
	}
}
