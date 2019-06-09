package de.scandio.e4.worker.model;

public class E4Error {

	private String key;
	private String type;
	protected String virtualUser;
	protected String action;

	public E4Error(String key, String type, String virtualUser, String action) {
		this.key = key;
		this.type = type;
		this.virtualUser = virtualUser;
		this.action = action;
	}

	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	public String getVirtualUser() {
		return virtualUser;
	}

	public String getAction() {
		return action;
	}

	@Override
	public String toString() {
		return String.format("%s|%s|%s|%s", key, type, virtualUser, action);
	}
}
