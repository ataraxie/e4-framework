package de.scandio.e4.worker.interfaces;

import java.util.List;

/**
 * One instance of Selenium.
 * Represents a user when thinking about user load.
 *
 * The scenarios should be run in a separate thread.
 */
public interface VirtualUser {
	List<Class<Scenario>> getScenarios();
}
