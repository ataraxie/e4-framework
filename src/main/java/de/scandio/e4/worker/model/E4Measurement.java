package de.scandio.e4.worker.model;

public class E4Measurement extends E4Model {

	private Long timeTaken;
	private String nodeId;

	public E4Measurement(Long timeTaken, String threadId,
						 String virtualUser, String action, String nodeId, String testPackage) {
		super(threadId, virtualUser, action, testPackage);
		this.timeTaken = timeTaken;
		this.nodeId = nodeId;
	}

	public Long getTimeTaken() {
		return timeTaken;
	}

	public String getNodeId() {
		return nodeId;
	}

	@Override
	public String toString() {
		return String.format("%s|%s|%s|%s|%s", timeTaken, threadId, virtualUser, action, nodeId);
	}
}
