package de.scandio.e4.client;

import java.util.List;

public class WorkerStatusResponse {
	private boolean areTestsRunning;
	private boolean arePreparationsFinished;
	private List<String> storedUsers;

	public boolean areTestsRunning() {
		return areTestsRunning;
	}

	public void setAreTestsRunning(boolean areTestsRunning) {
		this.areTestsRunning = areTestsRunning;
	}

	public boolean arePreparationsFinished() {
		return arePreparationsFinished;
	}

	public void setArePreparationsFinished(boolean arePreparationsFinished) {
		this.arePreparationsFinished = arePreparationsFinished;
	}

	public List<String> getStoredUsers() {
		return storedUsers;
	}

	public void setStoredUsers(List<String> storedUsers) {
		this.storedUsers = storedUsers;
	}
}
