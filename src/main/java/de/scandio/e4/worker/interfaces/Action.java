package de.scandio.e4.worker.interfaces;

import de.scandio.e4.worker.util.WorkerUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Action {

	private static Random RAND = new Random();

	private Logger log = LoggerFactory.getLogger(Action.class);

	public abstract void execute(@NotNull WebClient webClient, @NotNull RestClient restClient) throws Exception;

	public void executeWithRandomDelay(@NotNull WebClient webClient, @NotNull RestClient restClient) throws Exception {
		long randomWait = 2000 + RAND.nextInt(8000);
		log.info("Next action. Sleeping for {{}}ms", randomWait);
//		log.info("SKIPPING SLEEP FEATURE!");
		Thread.sleep(randomWait);
		execute(webClient, restClient);
	}

	public abstract long getTimeTaken();

	public boolean isRestOnly() {
		return false;
	}

	public String getNodeId(@NotNull WebClient webClient) {
		return webClient.getNodeId();
	}

	protected String rnd(String... values) {
		return rnd(Arrays.asList(values));
	}
	protected String rnd(List<String> values) {
		return WorkerUtils.getRandomItem(values);
	}

}
