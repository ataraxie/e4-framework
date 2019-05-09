package de.scandio.e4;

import org.apache.commons.cli.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class E4Application {

	public static void main(String[] args) {
		final CommandLine parsedArgs = parseArgs(args);

		if (parsedArgs.hasOption("worker-only")) {
			System.out.println("Starting E4 Worker only...");
			SpringApplication.run(E4Application.class, args);

		} else {
			System.out.println("Starting E4 Client...");

			final String configPath = parsedArgs.getOptionValue("config");

			if (configPath == null) {
				System.out.println("Not starting E4 in worker-only mode means you have to supply a config file. See --help for usage.");
				System.exit(1);
			}

			System.out.println(configPath);
			// make rest calls to worker nodes from config
		}
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

		final Option workerOnlyOption = new Option("w", "worker-only", false, "Run this E4 instance in worker-only-mode and listen for commands from an E4 client.");
		workerOnlyOption.setRequired(false);
		options.addOption(workerOnlyOption);

		final CommandLineParser parser = new DefaultParser();

		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			new HelpFormatter().printHelp("utility-name", options);
			System.exit(1);
		}

		return null;
	}
}
