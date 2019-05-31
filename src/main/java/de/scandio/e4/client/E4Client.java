package de.scandio.e4.client;

import de.scandio.e4.E4Application;
import de.scandio.e4.client.config.ClientConfig;
import de.scandio.e4.client.config.ConfigUtil;
import de.scandio.e4.client.orchestration.OrchestrationUtil;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class E4Client {

	private Logger log = LoggerFactory.getLogger(E4Client.class);

	private final CommandLine parsedArgs;

	public E4Client(CommandLine parsedArgs) {
		this.parsedArgs = parsedArgs;
	}

	public void start() throws Exception {
		final String configPath = parsedArgs.getOptionValue("config");
		final ClientConfig clientConfig = ConfigUtil.readConfigFromFile(configPath);

		log.info(clientConfig.toString());

		// TODO: validate config instead of just throwing exceptions in getters

		final List<String> workers = clientConfig.getWorkers();

		if (workers == null || workers.size() == 0) {
			log.info("No workers provided, starting a local worker...");
			orchestrateLocal(clientConfig);
		} else {
			log.info("Found remote workers! Let's see if we can connect to them.");
			orchestrateRemote(clientConfig);
		}
	}

	private void orchestrateLocal(ClientConfig clientConfig) throws Exception {
		final HashMap<String, Object> props = new HashMap<String, Object>(){{
			if (parsedArgs.hasOption("port")) {
				put("server.port", parsedArgs.getOptionValue("port"));
			} else {
				put("server.port", 0); // random port
			}

			if (parsedArgs.hasOption("screenshots-dir")) {
				put("screenshots.dir", parsedArgs.getOptionValue("screenshots-dir"));
				log.info("Set custom screenshots dir: " + get("screenshots.dir"));
			}
		}};

		final ConfigurableApplicationContext run = new SpringApplicationBuilder()
				.sources(E4Application.class)
				.properties(props)
				.run();

		final String localWorkerPort = run.getEnvironment().getProperty("local.server.port");
		final String localWorkerURL = "http://localhost:" + localWorkerPort + "/";

		log.info("Started local worker at: " + localWorkerURL);
		log.info("Checking if local worker is healthy via: "+localWorkerURL+"e4/status");

		final int statusCode = WorkerRestUtil.getStatus(localWorkerURL).getStatusCodeValue();

		if (statusCode == 200) {
			log.info("Local worker is healthy and enjoying itself!");
			clientConfig.setWorkers(Collections.singletonList(localWorkerURL));
			OrchestrationUtil.executePhases(clientConfig);
			System.exit(0);
		} else {
			log.info("Local worker is unhealthy. Status code was: " + statusCode);
			log.info("Aborting...");
			System.exit(1);
		}
	}

	private void orchestrateRemote(ClientConfig clientConfig) throws Exception {
		for (String workerURL: clientConfig.getWorkers()) {
			try {
				final int statusCode = WorkerRestUtil.getStatus(workerURL).getStatusCodeValue();
				if (statusCode == 200) {
					log.info(workerURL + "e4/status returned 200 - OK!");
				} else {
					throw new Exception("Status code wasn't 200 but " + statusCode);
				}
			} catch (Exception ex) {
				log.info("Worker unavailable: "+workerURL);
				log.info(ex.getMessage());
				System.exit(1);
			}
		}

		OrchestrationUtil.executePhases(clientConfig);
	}
}
