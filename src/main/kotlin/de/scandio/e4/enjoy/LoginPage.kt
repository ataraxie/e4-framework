package de.scandio.e4.enjoy

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class LoginPage(
    private val driver: WebDriver
) {
    private val loginFormLocator = By.name("loginform")

    fun logIn(
        user: User
    ): DashboardPage {
        driver.wait(
            Duration.ofSeconds(20),
            ExpectedConditions.presenceOfElementLocated(loginFormLocator)
        )
        val loginForm = driver.findElement(loginFormLocator)
        loginForm.findElement(By.name("os_username")).sendKeys(user.name)
        loginForm.findElement(By.name("os_password")).sendKeys(user.password)
        loginForm.findElement(By.id("loginButton")).click()
        return DashboardPage(driver)
    }
}