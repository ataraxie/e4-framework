package de.scandio.e4.setup

import org.junit.Test
import java.util.concurrent.TimeoutException

class DisableMarketplaceConnectivity : SetupBaseTest() {

    @Test
    fun test() {
        try {
            webConfluence.login()
            webConfluence.authenticateAdmin()
            webConfluence.navigateTo("plugins/servlet/upm")
            dom.click("#link-bar-settings a", 30)
            dom.click("#upm-checkbox-pacDisabled", 30)
            dom.click("#upm-settings-dialog .aui-button.confirm")
            shot()
        } catch (e: Exception) {
            shot()
        }

    }

}