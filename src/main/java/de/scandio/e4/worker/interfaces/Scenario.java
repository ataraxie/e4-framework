package de.scandio.e4.worker.interfaces;

import org.jetbrains.annotations.NotNull;

public interface Scenario {

	void execute(@NotNull WebClient webClient, @NotNull RestClient restClient) throws Exception;

}
