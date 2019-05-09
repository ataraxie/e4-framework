package de.scandio.e4.client;

import org.apache.commons.cli.CommandLine;

public class E4Client {
    private final CommandLine parsedArgs;

    public E4Client(CommandLine parsedArgs) {
        this.parsedArgs = parsedArgs;
    }

    public void enjoy() {
        final String configPath = parsedArgs.getOptionValue("config");

        if (configPath == null) {
            System.out.println("Not starting E4 in worker-only mode means you have to supply a config file. See --help for usage.");
            System.exit(1);
        }

        System.out.println(configPath);
        // make rest calls to worker nodes from config
    }
}
