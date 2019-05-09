package de.scandio.e4.enjoy

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.net.URI
import java.net.URLEncoder

class WebConfluence (
    val driver: WebDriver,
    val base: URI,
    private val adminPassword: String
    ) {

        fun goToLogin(): LoginPage {
            navigateTo("login.jsp")
            return LoginPage(driver)
        }

        fun goToSystemInfo() {
            navigateTo("secure/admin/ViewSystemInfo.jspa")
        }

        fun navigateTo(path: String) {
            driver.navigate().to(base.resolve(path).toURL())
        }
}