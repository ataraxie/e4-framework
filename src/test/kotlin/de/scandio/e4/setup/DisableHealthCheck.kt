package de.scandio.e4.setup

import org.junit.Test
import java.util.concurrent.TimeoutException

class DisableHealthCheck : SetupBaseTest() {

    @Test
    fun test() {
        try {
            webConfluence.disablePlugin("com.atlassian.troubleshooting.plugin-confluence")
        } catch (e: TimeoutException) {
            shot()
        }

    }

}