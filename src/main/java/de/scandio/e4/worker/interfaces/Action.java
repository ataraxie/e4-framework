package de.scandio.e4.worker.interfaces;

import org.jetbrains.annotations.NotNull;

public interface Action {

	void execute(@NotNull WebClient webClient, @NotNull RestClient restClient) throws Exception;

	// action ABCD took 50ms
	long getTimeTaken();
}
