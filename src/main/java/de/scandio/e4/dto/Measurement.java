package de.scandio.e4.dto;

import java.util.Date;

public class Measurement {

	private long timestamp;
	private Long timeTaken;
	private String threadId;
	private String virtualUser;
	private String nodeId;
	private String action;
	private String testPackage;

	public Measurement(Long timeTaken, String threadId,
					   String virtualUser, String action, String nodeId, String testPackage) {
		this.timestamp = new Date().getTime();
		this.timeTaken = timeTaken;
		this.threadId = threadId;
		this.virtualUser = virtualUser;
		this.action = action;
		this.nodeId = nodeId;
		this.testPackage = testPackage;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Long getTimeTaken() {
		return timeTaken;
	}

	public String getThreadId() {
		return threadId;
	}

	public String getVirtualUser() {
		return virtualUser;
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getAction() {
		return action;
	}

	public String getTestPackage() {
		return testPackage;
	}
}
