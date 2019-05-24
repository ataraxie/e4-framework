package de.scandio.e4;

import de.scandio.e4.confluence.web.WebConfluence;
import de.scandio.e4.worker.confluence.rest.RestConfluence;
import de.scandio.e4.worker.util.WorkerUtils;

public class E4TestRun {

	private static final String URL = "http://contabo:8090/";
	private static final String OUT_DIR = "/tmp/selenium";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";

	public static void main(String[] args) throws Exception {
		WebConfluence webConfluence = (WebConfluence) WorkerUtils.newPhantomJsWebClient(URL, OUT_DIR, USERNAME, PASSWORD);
		RestConfluence restConfluence = new RestConfluence(URL, USERNAME, PASSWORD);
		PlaygroundScenario playgroundScenario = new PlaygroundScenario(webConfluence, restConfluence);
		playgroundScenario.execute();
	}

}
