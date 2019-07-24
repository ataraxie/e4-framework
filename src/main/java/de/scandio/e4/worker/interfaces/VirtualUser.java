package de.scandio.e4.worker.interfaces;

import de.scandio.e4.worker.collections.ActionCollection;
import de.scandio.e4.worker.util.UserCredentials;

/**
 * One instance of Selenium.
 * Represents a user when thinking about user load.
 *
 * The actions should be run in a separate thread.
 */
public abstract class VirtualUser {

	protected UserCredentials impersonatedUser;

	public abstract ActionCollection getActions();

	public boolean isAdminRequired() {
		return false;
	}

	public UserCredentials getImpersonatedUser() {
		return impersonatedUser;
	}

	public void setImpersonatedUser(UserCredentials impersonatedUser) {
		this.impersonatedUser = impersonatedUser;
	}

	public void onInit(RestClient initialRestClient) {}

}
