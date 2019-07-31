package de.scandio.e4.client.config;

public class WorkerConfig {
	private String target;
	private String username;
	private String password;
	private String testPackage;
	private boolean repeatTests;
	private int numConcurrentUsers;
	private int numWorkers;
	private long durationInSeconds;
	private String logLevel;


	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTestPackage() {
		return testPackage;
	}

	public void setTestPackage(String testPackage) {
		this.testPackage = testPackage;
	}

	public boolean isRepeatTests() {
		return repeatTests;
	}

	public void setRepeatTests(boolean repeatTests) {
		this.repeatTests = repeatTests;
	}

	public void setNumConcurrentUsers(int numConcurrentUsers) {
		this.numConcurrentUsers = numConcurrentUsers;
	}

	public void setNumWorkers(int numWorkers) {
		this.numWorkers = numWorkers;
	}

	public int getNumConcurrentUsers() {
		return numConcurrentUsers;
	}

	public int getNumWorkers() {
		return numWorkers;
	}

	public long getDurationInSeconds() {
		return durationInSeconds;
	}

	public void setDurationInSeconds(long durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public static WorkerConfig from(ClientConfig clientConfig) {
		final WorkerConfig workerConfig = new WorkerConfig();

		workerConfig.setTarget(clientConfig.getTarget().getUrl());
		workerConfig.setUsername(clientConfig.getTarget().getAdminUser());
		workerConfig.setPassword(clientConfig.getTarget().getAdminPassword());

		workerConfig.setRepeatTests(clientConfig.getDurationInSeconds() > 0);
		workerConfig.setDurationInSeconds(clientConfig.getDurationInSeconds());
		workerConfig.setNumConcurrentUsers(clientConfig.getNumConcurrentUsers());
		workerConfig.setNumWorkers(clientConfig.getWorkers().size());
		workerConfig.setTestPackage(clientConfig.getTestPackage());
		workerConfig.setLogLevel(clientConfig.getLogLevel());

		return workerConfig;
	}

	@Override
	public String toString() {
		return "WorkerConfig{" +
				"target='" + target + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", testPackage='" + testPackage + '\'' +
				", repeatTests=" + repeatTests +
				", numConcurrentUsers=" + numConcurrentUsers +
				'}';
	}
}
