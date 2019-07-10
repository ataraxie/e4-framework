package de.scandio.e4;

import de.scandio.e4.worker.client.ApplicationName;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class E4 {

	// BEGIN: REQUIRED
	public static final ApplicationName APPLICATION_NAME = ApplicationName.valueOf(getenv("E4_APPLICATION_NAME"));
	public static final String APPLICATION_BASE_URL = resolveBaseUrl("E4_APPLICATION_BASE_URL");
	public static final String OUT_DIR = getenv("E4_OUT_DIR");
	public static final String IN_DIR = getenv("E4_IN_DIR");
	public static final String ADMIN_USERNAME = getenv("E4_ADMIN_USER");
	public static final String ADMIN_PASSWORD = getenv("E4_ADMIN_PWD");
	public static final String APPLICATION_VERSION = getenv("E4_APPLICATION_VERSION");
	public static final String APPLICATION_VERSION_DOT_FREE = APPLICATION_VERSION.replace(".", "");
	// END: REQUIRED

	public static final String APPLICATION_LICENSE = getenv("E4_APPLICATION_LICENSE", false);
	public static final boolean PREPARATION_RUN = "true".equals(getenv("E4_PREPARATION_RUM", false));
	public static final String APP_VERSION = getenv("E4_APP_VERSION", false);
	public static final String APP_LICENSE = getenv("E4_APP_LICENSE", false);

	private static String resolveBaseUrl(String varName) {
		String url = getenv(varName);
		return url.endsWith("/") ? url : url + "/";
	}

	public static String getenv(String envVarKey, boolean required) throws IllegalArgumentException {
		String envVarValue = System.getenv(envVarKey);
		if (required && StringUtils.isBlank(envVarValue)) {
			throw new IllegalArgumentException("Environment variable needs to be set: " + envVarKey);
		}
		return envVarValue;
	}

	private static String getenv(String envVarKey) {
		return getenv(envVarKey, true);
	}
}