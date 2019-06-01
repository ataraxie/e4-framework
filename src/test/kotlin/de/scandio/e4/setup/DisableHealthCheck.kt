package de.scandio.e4.setup

import org.junit.Test
import java.util.concurrent.TimeoutException

class DisableHealthCheck : SetupBaseTest() {

    @Test
    fun test() {
        try {
            webConfluence.login()
            shot()
            webConfluence.authenticateAdmin()
            val upmRowSelector = ".upm-plugin[data-key='com.atlassian.troubleshooting.plugin-confluence']"
            println("Disabling health checks (Troubleshooting & Support plugin)")
            goTo("plugins/servlet/upm/manage/all")
            shot()
            dom.awaitElementPresent(".upm-plugin-list-container", 20)
            shot()
            dom.click(upmRowSelector)
            dom.click("$upmRowSelector .aui-button[data-action='DISABLE']")
            dom.awaitElementPresent("$upmRowSelector .aui-button[data-action='ENABLE']")
            println("--> SUCCESS")
        } catch (e: TimeoutException) {
            shot()
        }

    }

}