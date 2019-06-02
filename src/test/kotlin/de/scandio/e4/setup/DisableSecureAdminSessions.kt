package de.scandio.e4.setup

import org.junit.Test
import java.util.concurrent.TimeoutException

class DisableSecureAdminSessions : SetupBaseTest() {

    @Test
    fun test() {
        try {
            webConfluence.disableSecureAdminSessions()
        } catch (e: TimeoutException) {
            shot()
        }
    }
}