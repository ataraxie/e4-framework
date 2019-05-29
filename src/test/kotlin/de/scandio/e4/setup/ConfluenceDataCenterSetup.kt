package de.scandio.e4.setup

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.enjoy.wait
import de.scandio.e4.util.Util
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.WebElement
import java.awt.SystemColor.window
import java.util.concurrent.TimeUnit


/*
Search/Replace for Java export from Selenium Chrome addon:

driver.findElement\(By.id\("(.*)"\)\).sendKeys\("(.*)"\)
dom.insertText("#$1", "$2")


driver.findElement\(By.id\("(.*)"\)\)\.click\(\)
dom.click("#$1")
 */

class ConfluenceDataCenterSetup {

    private val BASE_URL = "http://e4-test:8090/"
    private val OUT_DIR = "/tmp/e4/out"
    private val USERNAME = "admin1"
    private val PASSWORD = "admin1"

    private val driver: WebDriver
    private val util: Util
    private val dom: DomHelper

    init {
        WebDriverManager.chromedriver().setup()
        val chromeOptions = ChromeOptions()
        chromeOptions.addArguments("--headless")
        this.driver = ChromeDriver(chromeOptions)
        this.util = Util()
        this.dom = DomHelper(driver, 120, 120)
    }

    @Before
    fun before() {

    }

    @After
    fun tearDown() {
        driver.quit()
    }

    @Test
    fun test() {
        dom.click("#custom .plugin-disabled-icon")
        dom.click("#setup-next-button")
        dom.await(5000)
        dom.click("#setup-next-button")
        dom.click("#confLicenseString")
        dom.insertText("#confLicenseString", "AAABLw0ODAoPeNp9kF9PgzAUxd/7KZr4og8sg8mYS0g0QJQEmJHpky933WU2YWXpn2V8ewt1mZroW\n3tue37nnquyE7SEngYR9RfLMFqGM5qkaxpM/TuSdEID0xXsMW6w5afJThqxvVcMxJZ3ky0S1olmY\nt/wI8ZaGiTPRrIPUJiCxnhw8aahFyxIwRkKhdnpwGX/bTj3gugMykrg7b8k+xESFBqlo9Vmo5jkB\n8074RRrYccCBPuDNfpUZr9BuWpeFUoVe75Taw1ysG6gVXhOnKdxkad1VnmFP5tHt5G/IPYW/1RWc\ngeCKxiD1C41fdxvnkgicVR/FzISvxjr/oBjy8mqLLOXJH8oSOtGbzbg4BmQFC+r2sKa1qBdkl4Pn\nVBXys37kmZHaM1IJJej6+YTcDek9jAsAhQMjndzQwNXokcsfeEbtiQJn5ZfSwIUaMVmEklmaIX9V\n0zou8i5649ihAg=X02f7")
        dom.click("#setupTypeCustom")
        dom.click("#setupTypeCustom")
        dom.insertText("#clusterName", "confluence-cluster")
        dom.insertText("#clusterHome", "/confluence-shared-home")
        dom.click("#cluster-auto-address")
        dom.await(1000)
        dom.insertText("#clusterAddressString", "230.0.0.1")
        dom.click("#cluster-address-field > .field-group")
        dom.await(1000)
        dom.click("[name='newCluster']")
        dom.insertText("dbConfigInfo-hostname", "confluence-cluster-6153-db")
        dom.insertText("dbConfigInfo-port", "5432")
        dom.insertText("dbConfigInfo-databaseName", "confluence")
        dom.insertText("dbConfigInfo-username", "confluence")
        dom.insertText("dbConfigInfo-password", "confluence")
        dom.click("#testConnection")
        dom.await(5000)
        dom.click("#setup-next-button")

        dom.insertText("#fullName", "Administrator")
        dom.insertText("#email", "admin@example.com")
        dom.insertText("#password", "admin")
        dom.insertText("#confirm", "admin")
        dom.click("#setup-next-button")
        driver.findElement(By.linkText("Start")).click()

        dom.insertText("#grow-intro-space-name", "TEST")
        dom.click("#grow-intro-create-space")
        dom.click("#onboarding-skip-editor-tutorial")
        dom.click("#editor-precursor > .cell")
        dom.click("#content-title")
        dom.insertText("#content-title", "Test Page")
        dom.click("#rte-button-publish")
        dom.click(".aui-avatar-inner > img")
        dom.click("#main")
    }

}