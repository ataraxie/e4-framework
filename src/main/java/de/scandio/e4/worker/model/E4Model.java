package de.scandio.e4.worker.model;

import java.util.Date;

public abstract class E4Model {
	protected long timestamp;
	protected String threadId;
	protected String virtualUser;
	protected String action;
	protected String testPackage;

	public E4Model(String threadId, String virtualUser, String action, String testPackage) {
		this.timestamp = new Date().getTime();
		this.threadId = threadId;
		this.virtualUser = virtualUser;
		this.action = action;
		this.testPackage = testPackage;
	}

	public long getTimestamp() {
		return timestamp;
	}


	public String getThreadId() {
		return threadId;
	}

	public String getVirtualUser() {
		return virtualUser;
	}

	public String getAction() {
		return action;
	}

	public String getTestPackage() {
		return testPackage;
	}
}
