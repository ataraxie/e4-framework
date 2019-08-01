package de.scandio.e4;

import de.scandio.e4.worker.client.ApplicationName;
import de.scandio.e4.worker.factories.ClientFactory;
import de.scandio.e4.worker.interfaces.RestClient;
import de.scandio.e4.worker.interfaces.WebClient;
import org.apache.commons.lang3.StringUtils;

public class E4TestEnv {

	public static final ApplicationName APPLICATION_NAME = ApplicationName.valueOf(getenv("E4_APPLICATION_NAME", "confluence"));
	public static final String APPLICATION_BASE_URL = resolveBaseUrl("E4_APPLICATION_BASE_URL", "http://confluence-cluster-6153-lb:26153/");
	public static final String OUT_DIR = getenv("E4_OUT_DIR", "./target/out");
	public static final String IN_DIR = getenv("E4_IN_DIR", "./target/in");
	public static final String USER_NAME = getenv("E4_USER_NAME", "admin");
	public static final String USER_PASSWORD = getenv("E4_USER_PASSWORD", "admin");
	public static final String APPLICATION_VERSION = getenv("E4_APPLICATION_VERSION", "6.15.3");
	public static final String APPLICATION_VERSION_DOT_FREE = APPLICATION_VERSION.replace(".", "");

	public static final String APPLICATION_LICENSE = getenv("E4_APPLICATION_LICENSE", "");
	public static final boolean PREPARATION_RUN = "true".equals(getenv("E4_PREPARATION_RUM", "false"));
	public static final String APP_VERSION = getenv("E4_APP_VERSION", "");
	public static final String APP_LICENSE = getenv("E4_APP_LICENSE", "");

	public static String resolveBaseUrl(String varName, String defaultValue) {
		String url = getenv(varName, defaultValue);
		return url.endsWith("/") ? url : url + "/";
	}

	public static String getenv(String envVarKey, boolean required, String defaultValue) throws IllegalArgumentException {
		String envVarValue = System.getenv(envVarKey);
		if (StringUtils.isBlank(envVarValue)) {
			if (required) {
				throw new IllegalArgumentException("Environment variable needs to be set: " + envVarKey);
			} else {
				envVarValue = defaultValue;
			}
		}
		return envVarValue;
	}

	public static String getenv(String envVarKey, String defaultValue) {
		return getenv(envVarKey, false, defaultValue);
	}

	public static String getenv(String envVarKey) {
		return getenv(envVarKey, false, null);
	}

	public static WebClient newAdminTestWebClient() throws Exception {
		return ClientFactory.newChromeWebClient(APPLICATION_NAME, APPLICATION_BASE_URL,
				IN_DIR, OUT_DIR, USER_NAME, USER_PASSWORD);
	}

	public static RestClient newAdminTestRestClient() {
		return ClientFactory.newRestClient(APPLICATION_NAME, null,APPLICATION_BASE_URL,
				USER_NAME, USER_PASSWORD);
	}
}
