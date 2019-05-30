package de.scandio.e4.setup

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.worker.util.Util
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.By
import org.openqa.selenium.Dimension


/*
Search/Replace for Java export from Selenium Chrome addon:

driver.findElement\(By.id\("(.*)"\)\).sendKeys\("(.*)"\)
dom.insertText("#$1", "$2")


driver.findElement\(By.id\("(.*)"\)\)\.click\(\)
dom.click("#$1")
 */

class ConfluenceDataCenterSetup {

    private val BASE_URL = "http://confluence-cluster-6153-lb:26153/"
    private val OUT_DIR = "/tmp/e4/out"
    private val USERNAME = "admin"
    private val PASSWORD = "admin"

    private val driver: WebDriver
    private val util: Util
    private val dom: DomHelper

    private var screenshotCount = 0

    init {
        WebDriverManager.chromedriver().setup()
        val chromeOptions = ChromeOptions()
        chromeOptions.addArguments("--headless")
        this.driver = ChromeDriver(chromeOptions)
        this.driver.manage().window().setSize(Dimension(1680, 1050))
        this.util = Util()
        this.dom = DomHelper(driver, 120, 120)
        this.dom.defaultDuration = 120
        this.dom.defaultWaitTillPresent = 120
        this.dom.outDir = OUT_DIR
        this.dom.screenshotBeforeClick = true
        this.dom.screenshotBeforeInsert = true
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
        driver.navigate().to(BASE_URL)
        dom.await(1000) // just wait a bit for safety

//        /* Step 1: Test vs. Production */
//        dom.awaitElementPresent("[name='startform']")
//        dom.click("#custom .plugin-disabled-icon")
//        dom.click("#setup-next-button")
//
//        /* Step 2: Apps */
//        dom.awaitElementPresent("[bundle-id='com.atlassian.confluence.plugins.confluence-questions']")
//        dom.click("#setup-next-button")
//
//        /* Step 3: License */
//        dom.click("#confLicenseString")
//        dom.insertText("#confLicenseString", "AAABLw0ODAoPeNp9kF9PgzAUxd/7KZr4og8sg8mYS0g0QJQEmJHpky933WU2YWXpn2V8ewt1mZroW\n3tue37nnquyE7SEngYR9RfLMFqGM5qkaxpM/TuSdEID0xXsMW6w5afJThqxvVcMxJZ3ky0S1olmY\nt/wI8ZaGiTPRrIPUJiCxnhw8aahFyxIwRkKhdnpwGX/bTj3gugMykrg7b8k+xESFBqlo9Vmo5jkB\n8074RRrYccCBPuDNfpUZr9BuWpeFUoVe75Taw1ysG6gVXhOnKdxkad1VnmFP5tHt5G/IPYW/1RWc\ngeCKxiD1C41fdxvnkgicVR/FzISvxjr/oBjy8mqLLOXJH8oSOtGbzbg4BmQFC+r2sKa1qBdkl4Pn\nVBXys37kmZHaM1IJJej6+YTcDek9jAsAhQMjndzQwNXokcsfeEbtiQJn5ZfSwIUaMVmEklmaIX9V\n0zou8i5649ihAg=X02f7")
//        dom.click("#setupTypeCustom")
//
//        /* Step 4: Cluster configuration */
//        dom.insertText("#clusterName", "confluence-cluster")
//        dom.insertText("#clusterHome", "/confluence-shared-home")
//        dom.click("#cluster-auto-address")
//        dom.insertText("#clusterAddressString", "230.0.0.1")
//        dom.click("[name='newCluster']")
//
//        // Step 5: DB config */
//        dom.insertText("#dbConfigInfo-hostname", "confluence-cluster-6153-db")
//        dom.insertText("#dbConfigInfo-port", "5432")
//        dom.insertText("#dbConfigInfo-databaseName", "confluence")
//        dom.insertText("#dbConfigInfo-username", "confluence")
//        dom.insertText("#dbConfigInfo-password", "confluence")
//        dom.click("#testConnection")
//        dom.awaitElementVisible("#setupdb-successMessage")
//        dom.click("#setup-next-button")
//
//        /* This takes a few minutes! Make sure the next step has a wait value! */
//        println("Database setup in progress. This takes a while. Waiting for 5 minutes. Grab some coffee...")
//        dom.await(6000) // wait 10 minutes!

        /* Step 6: Setup data */
        dom.click("input[Value='Empty Site']")

        // Step 7: User management */
        dom.click("#internal")

        /* Step 8: Admin account */

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
        dom.awaitElementPresent("#main-content")
    }

    private fun shot() {
        this.screenshotCount += 1
        this.util.takeScreenshot(driver, "$OUT_DIR/$screenshotCount-confluence-data-center-setup.png")
    }

}