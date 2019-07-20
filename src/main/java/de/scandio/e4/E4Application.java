package de.scandio.e4;

import de.scandio.e4.client.E4Client;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import java.util.HashMap;
import java.util.logging.Level;

@SpringBootApplication
public class E4Application {

	private static final Logger log = LoggerFactory.getLogger(E4Application.class);

	public static void main(String[] args) {
		final CommandLine parsedArgs = parseArgs(args);
		java.security.Security.setProperty("networkaddress.cache.ttl" , "60");
		System.setProperty("webdriver.chrome.silentOutput", "true");
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Shutdown signal received.. shutting down threads.");
			Thread.getAllStackTraces().keySet().forEach(thread -> {
				try {
					thread.interrupt();
				} catch (Exception ex) {
					log.info("Error interrupting thread " + thread);
					ex.printStackTrace();
				}
			});
		}));

		try {
			if (parsedArgs.hasOption("worker-only")) {
				startWorkerOnly(parsedArgs);
			} else {
				startClient(parsedArgs);
			}
		} catch (Exception ex) {
			log.info("Encountered unenjoyable exception:");
			ex.printStackTrace();
			System.exit(1);
		}
	}

	public static void startClient(CommandLine parsedArgs) throws Exception {
		log.info("E4 Client... Enjoy!");
		final E4Client e4Client = new E4Client(parsedArgs);
		e4Client.start();
	}

	public static void startWorkerOnly(CommandLine parsedArgs) {
		String port = parsedArgs.getOptionValue("port");

		// we could also check if the port is actually a valid port - not necessary right now
		if (port == null) {
			port = "4444";
		}

		log.info("E4 in worker-only mode... Enjoy!");

		final HashMap<String, Object> props = new HashMap<>();
		props.put("server.port", port);

		if (parsedArgs.hasOption("output-dir")) {
			props.put("output.dir", parsedArgs.getOptionValue("output-dir"));
			log.info("Set custom output dir: " + props.get("output.dir"));
		}

		if (parsedArgs.hasOption("input-dir")) {
			props.put("input.dir", parsedArgs.getOptionValue("input-dir"));
			log.info("Set custom input dir: " + props.get("input.dir"));
		}

		new SpringApplicationBuilder()
				.sources(E4Application.class)
				.properties(props)
				.run();

		log.info("E4 Worker is running on: http://localhost:"+port+"/ and waiting for commands.");
	}

	/**
	 * Parses the arguments and returns them or shuts down the application if an error occours.
	 * @param args The program args.
	 */
	private static CommandLine parseArgs(String[] args) {
		final Options options = new Options();

		final Option configOption = new Option("c", "config", true, "Path to a config JSON file. Required if you're not starting in worker-only mode.");
		configOption.setRequired(false);
		options.addOption(configOption);

		final Option workerOnlyOption = new Option("w", "worker-only", false, "Run this E4 instance in worker-only-mode and listen for commands from an E4TestEnv client.");
		workerOnlyOption.setRequired(false);
		options.addOption(workerOnlyOption);

		final Option portOption = new Option("p", "port", true, "Port to run the E4 Worker on. Required for worker-only mode.");
		portOption.setRequired(false);
		options.addOption(portOption);

		final Option outputDirOption = new Option("o", "output-dir", true, "Directory to save outputs to. By default this will be './outputs'.");
		outputDirOption.setRequired(false);
		options.addOption(outputDirOption);

		final Option inputDirOption = new Option("i", "input-dir", true, "Directory where input files are saved. By default this will be './inputs'.");
		inputDirOption.setRequired(false);
		options.addOption(inputDirOption);

		final Option skipValidationOption = new Option("s", "skip-validation", false, "Skip validation of test package");
		outputDirOption.setRequired(false);
		options.addOption(skipValidationOption);

		final CommandLineParser parser = new DefaultParser();

		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			log.info(e.getMessage());
			new HelpFormatter().printHelp("java -jar your-e4.jar", options);
			System.exit(1);
		}

		return null;
	}
}
