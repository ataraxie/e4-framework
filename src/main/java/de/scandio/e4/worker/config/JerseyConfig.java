package de.scandio.e4.worker.config;

import de.scandio.e4.worker.rest.E4Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		registerEndpoints();
	}

	private void registerEndpoints() {
		register(E4Resource.class);
	}
}