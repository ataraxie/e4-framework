package de.scandio.e4.worker.interfaces;

import org.jetbrains.annotations.NotNull;

public interface Scenario {

	void execute(@NotNull WebClient webClient, @NotNull RestClient restClient) throws Exception;

	// scenario ABCD took 50ms
	long getTimeTaken();


	void setUsername(String username);
	void setPassword(String setPassword);
}
