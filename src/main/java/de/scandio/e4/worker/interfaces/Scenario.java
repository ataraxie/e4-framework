package de.scandio.e4.worker.interfaces;

public interface Scenario {

	void execute() throws Exception;

	// scenario ABCD took 50ms
	long getTimeTaken();
}
