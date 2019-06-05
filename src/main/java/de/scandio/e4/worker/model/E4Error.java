package de.scandio.e4.worker.model;

public class E4Error {

	private String key;
	private String type;

	public E4Error(String key, String type) {
		this.key = key;
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("%s|%s", key, type);
	}
}
