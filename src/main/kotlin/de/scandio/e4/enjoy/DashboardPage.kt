package de.scandio.e4.enjoy

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class DashboardPage(
    private val driver: WebDriver
) {
    fun waitForDashboard(): DashboardPage {
        driver.wait(
            Duration.ofSeconds(60),
            ExpectedConditions.presenceOfElementLocated(By.className("page-type-dashboard"))
        )
        return this
    }
}