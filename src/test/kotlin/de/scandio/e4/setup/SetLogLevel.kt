package de.scandio.e4.setup

import org.junit.Test
import java.util.concurrent.TimeoutException

class SetLogLevel : SetupBaseTest() {

    @Test
    fun test() {
        try {
            webConfluence.setLogLevel("co.goodsoftware", "INFO")
            shot()
        } catch (e: Exception) {
            shot()
        }

    }

}