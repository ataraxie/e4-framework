package de.scandio.e4;

import de.scandio.e4.client.E4Client;
import org.apache.commons.cli.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class E4Application {

	public static void main(String[] args) {
		final CommandLine parsedArgs = parseArgs(args);

		if (parsedArgs.hasOption("worker-only")) {
			startWorkerOnly(parsedArgs);
		} else {
			startClient(parsedArgs);
		}
	}

	public static void startClient(CommandLine parsedArgs) {
		System.out.println("Starting E4 Client... Enjoy!");

		final E4Client e4Client = new E4Client(parsedArgs);
		e4Client.enjoy();
	}

	public static void startWorkerOnly(CommandLine parsedArgs) {
		String port = parsedArgs.getOptionValue("port");

		if (port == null) {
			port = "4444";
		} else {
			// TODO: we could check if the port is actually a legit port...
		}

		System.out.println("Starting E4 in worker-only mode on port "+port+"... Enjoy!");

		SpringApplication.run(E4Application.class, "Dserver.port="+port);
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

		final Option portOption = new Option("p", "port", true, "Port to run the E4 Worker on if running in worker-only mode.");
		portOption.setRequired(false);
		options.addOption(portOption);


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
