package de.scandio.e4.client.config;

import java.util.List;

public class ClientConfig {
	private TargetConfig target;
	private int numConcurrentUsers;
	private long durationInSeconds;
	private String testPackage;

	private List<String> appsToInstall;
	private List<String> workers;

	public TargetConfig getTarget() {
		if (target == null) {
			throw new IllegalArgumentException("A target is required.");
		}
		return target;
	}

	public void setTarget(TargetConfig target) {
		this.target = target;
	}

	public int getNumConcurrentUsers() {
		if (numConcurrentUsers <= 0) {
			numConcurrentUsers = 1;
		}
		return numConcurrentUsers;
	}

	public void setNumConcurrentUsers(int numConcurrentUsers) {
		this.numConcurrentUsers = numConcurrentUsers;
	}

	public long getDurationInSeconds() {
		// 0 = execute every test once?
		return durationInSeconds;
	}

	public void setDurationInSeconds(long durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}

	public String getTestPackage() {
		if (testPackage == null) {
			throw new IllegalArgumentException("The test package is required.");
		}

		try {
			Class.forName(testPackage);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("The test package "+testPackage+" could not be found.");
		}
		return testPackage;
	}

	public void setTestPackage(String testPackage) {
		this.testPackage = testPackage;
	}

	public List<String> getAppsToInstall() {
		return appsToInstall;
	}

	public void setAppsToInstall(List<String> appsToInstall) {
		this.appsToInstall = appsToInstall;
	}

	public List<String> getWorkers() {
		return workers;
	}

	public void setWorkers(List<String> workers) {
		this.workers = workers;
	}

	@Override
	public String toString() {
		return "ClientConfig {" +
				"\n\ttarget=" + target +
				",\n\tnumConcurrentUsers=" + numConcurrentUsers +
				",\n\tdurationInSeconds=" + durationInSeconds +
				",\n\ttestPackage='" + testPackage + '\'' +
				",\n\tappsToInstall=" + appsToInstall +
				",\n\tworkers=" + workers +
				"\n}";
	}

	public class TargetConfig {
		private String url;
		private String adminUser;
		private String adminPassword;

		public String getUrl() {
			if (url == null) {
				throw new IllegalArgumentException("The url of the target application is required.");
			}
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getAdminUser() {
			if (adminUser == null) {
				adminUser = "admin";
			}
			return adminUser;
		}

		public void setAdminUser(String adminUser) {
			this.adminUser = adminUser;
		}

		public String getAdminPassword() {
			if (adminPassword == null) {
				adminPassword = "admin";
			}
			return adminPassword;
		}

		public void setAdminPassword(String adminPassword) {
			this.adminPassword = adminPassword;
		}

		@Override
		public String toString() {
			return "TargetConfig {" +
					"\n\t\turl='" + url + '\'' +
					",\n\t\tadminUser='" + adminUser + '\'' +
					",\n\t\tadminPassword='" + adminPassword + '\'' +
					"\n\t}";
		}
	}
}
