package de.scandio.e4.client.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;

public class ConfigUtil {

	private static final Logger log = LoggerFactory.getLogger(ConfigUtil.class);

	public static ClientConfig readConfigFromFile(String configPath) {
		ClientConfig config = null;

		if (configPath == null) {
			log.info("Not starting E4 in worker-only mode means you have to supply a config file. See --help for usage.");
			System.exit(1);
		}

		try {
			JsonReader reader = new JsonReader(new FileReader(configPath));
			config = new Gson().fromJson(reader, ClientConfig.class);
		} catch (Exception e) {
			log.info("Error with config file: " + configPath);
			e.printStackTrace();
			System.exit(1);
		}

		return config;
	}
}
