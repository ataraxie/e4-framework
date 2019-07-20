package de.scandio.e4.clients

import org.openqa.selenium.WebDriver
import java.net.URI

class WebJira(
        driver: WebDriver,
        base: URI,
        inputDir: String,
        outputDir: String,
        username: String,
        password: String
): WebAtlassian(driver, base, inputDir, outputDir, username, password) {

    override fun login() {
        navigateTo("/login.jsp")
        dom.awaitElementPresent("#login-form-username")
        dom.insertText("#login-form-username", username)
        dom.insertText("#login-form-password", password)
        dom.click("#login-form-submit")
        dom.awaitElementPresent("#create_link")
    }

    override fun authenticateAdmin() {
        // NOT REQUIRED DUE TO WEBSUDO DISABLED
    }


}