package de.scandio.e4.clients.rest

import de.scandio.e4.worker.rest.RestAtlassian
import de.scandio.e4.worker.services.StorageService

class RestJira(
        storageService: StorageService,
        baseUrl: String,
        username: String,
        password: String) : RestAtlassian(storageService, baseUrl, username, password) {

    override fun getUsernames(): List<String>? {
        // TODO
        return null
    }

}
