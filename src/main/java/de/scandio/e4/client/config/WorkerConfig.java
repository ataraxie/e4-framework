package de.scandio.e4.client.config;

public class WorkerConfig {
	private String target;
	private String username;
	private String password;
	private String testPackage;
	private boolean repeatTeasts;
	private long virtualUsers;

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

	public boolean isRepeatTeasts() {
		return repeatTeasts;
	}

	public void setRepeatTeasts(boolean repeatTeasts) {
		this.repeatTeasts = repeatTeasts;
	}

	public long getVirtualUsers() {
		return virtualUsers;
	}

	public void setVirtualUsers(long virtualUsers) {
		this.virtualUsers = virtualUsers;
	}

	public static WorkerConfig from(ClientConfig clientConfig) {
		final WorkerConfig workerConfig = new WorkerConfig();

		workerConfig.setTarget(clientConfig.getTarget().getUrl());
		workerConfig.setUsername(clientConfig.getTarget().getAdminUser());
		workerConfig.setPassword(clientConfig.getTarget().getAdminPassword());

		workerConfig.setRepeatTeasts(clientConfig.getDurationInSeconds() > 0);
		workerConfig.setVirtualUsers(clientConfig.getConcurrentUsers() / clientConfig.getWorkers().size());
		workerConfig.setTestPackage(clientConfig.getTestPackage());

		return workerConfig;
	}

	@Override
	public String toString() {
		return "WorkerConfig{" +
				"target='" + target + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", testPackage='" + testPackage + '\'' +
				", repeatTeasts=" + repeatTeasts +
				", virtualUsers=" + virtualUsers +
				'}';
	}
}
