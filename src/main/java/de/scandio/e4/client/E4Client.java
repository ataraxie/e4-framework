package de.scandio.e4.client;

import de.scandio.e4.E4Application;
import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.client.config.ConfigUtil;
import org.apache.commons.cli.CommandLine;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
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
			orchestrateLocal(clientConfig);
		} else {
			System.out.println("Found remote workers! Let's see if we can connect to them.");
			orchestrateRemote(clientConfig);
		}
	}

	private void orchestrateLocal(ClientConfig clientConfig) {
		final HashMap<String, Object> props = new HashMap<String, Object>(){{
			if (parsedArgs.hasOption("port")) {
				put("server.port", parsedArgs.getOptionValue("port"));
			} else {
				put("server.port", 0); // random port
			}
		}};

		final ConfigurableApplicationContext run = new SpringApplicationBuilder()
				.sources(E4Application.class)
				.properties(props)
				.run();

		final String localWorkerPort = run.getEnvironment().getProperty("local.server.port");
		final String localWorkerURL = "http://localhost:" + localWorkerPort + "/";

		System.out.println("Started local worker at: " + localWorkerURL);
		System.out.println("Checking if local worker is healthy via: "+localWorkerURL+"e4/status");

		final int statusCode = WorkerRestUtil.getStatus(localWorkerURL).getStatusCodeValue();

		if (statusCode == 200) {
			System.out.println("Local worker is healthy and enjoying itself!");
			clientConfig.setWorkers(Collections.singletonList(localWorkerURL));
			OrchestrationUtil.orchestrateWorkers(clientConfig);
		} else {
			System.out.println("Local worker is unhealthy. Status code was: " + statusCode);
			System.out.println("Aborting...");
			System.exit(1);
		}
	}

	private void orchestrateRemote(ClientConfig clientConfig) {
		for (String workerURL: clientConfig.getWorkers()) {
			try {
				final int statusCode = WorkerRestUtil.getStatus(workerURL).getStatusCodeValue();
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

		OrchestrationUtil.orchestrateWorkers(clientConfig);
	}
}
