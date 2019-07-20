package de.scandio.e4.worker.rest;

import de.scandio.e4.worker.services.StorageService;

import java.util.List;

public class RestJira extends RestAtlassian {

	public RestJira(StorageService storageService, String baseUrl, String username, String password) {
		super(storageService, baseUrl, username, password);
	}

	@Override
	public List<String> getUsernames() {
		// TODO
		return null;
	}

}
