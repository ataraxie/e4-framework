package de.scandio.e4.client.orchestration.phases;

public interface OrchestrationPhase {
	void executePhase() throws Exception;

	// void wait() ?
	// boolean isFinished() ?
}
