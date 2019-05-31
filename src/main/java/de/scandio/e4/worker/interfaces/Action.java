package de.scandio.e4.worker.interfaces;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public abstract class Action {

	private Logger log = LoggerFactory.getLogger(Action.class);

	public abstract void execute(@NotNull WebClient webClient, @NotNull RestClient restClient) throws Exception;

	public void executeWithRandomDelay(@NotNull WebClient webClient, @NotNull RestClient restClient) throws Exception {
		long randomSleepTime = new Random().nextInt(5000);
		log.info("Next action. Sleeping for {{}}ms", randomSleepTime);
		Thread.sleep(randomSleepTime);
		execute(webClient, restClient);
	}

	public abstract long getTimeTaken();
}
