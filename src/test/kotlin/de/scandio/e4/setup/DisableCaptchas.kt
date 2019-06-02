package de.scandio.e4.setup

import org.junit.Test
import java.util.concurrent.TimeoutException

class DisableCaptchas : SetupBaseTest() {

    @Test
    fun test() {
        try {
            webConfluence.disableCaptchas()
        } catch (e: TimeoutException) {
            shot()
        }

    }
}