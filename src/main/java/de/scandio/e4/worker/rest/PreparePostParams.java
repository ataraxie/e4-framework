package de.scandio.e4.worker.rest;

import de.scandio.e4.client.config.WorkerConfig;

public class PreparePostParams {

	private WorkerConfig workerConfig;
	private int workerIndex;

	public PreparePostParams(WorkerConfig workerConfig, int workerIndex) {
		this.workerConfig = workerConfig;
		this.workerIndex = workerIndex;
	}

	public WorkerConfig getWorkerConfig() {
		return workerConfig;
	}

	public void setWorkerConfig(WorkerConfig workerConfig) {
		this.workerConfig = workerConfig;
	}

	public int getWorkerIndex() {
		return workerIndex;
	}

	public void setWorkerIndex(int workerIndex) {
		this.workerIndex = workerIndex;
	}

}
