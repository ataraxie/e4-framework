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
import org.slf4j.LoggerFactory


/*
Search/Replace for Java export from Selenium Chrome addon:

driver.findElement\(By.id\("(.*)"\)\).sendKeys\("(.*)"\)
dom.insertText("#$1", "$2")


driver.findElement\(By.id\("(.*)"\)\)\.click\(\)
dom.click("#$1")
 */

open class ConfluenceDataCenterSetupStage1 : SetupBaseTest() {

    private val log = LoggerFactory.getLogger(javaClass)

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
        dom.awaitSeconds(3) // just wait a bit for safety

        /* Step 1: Test vs. Production */
        dom.awaitElementPresent("[name='startform']")
        dom.click("#custom .plugin-disabled-icon")
        dom.click("#setup-next-button")

        /* Step 2: Apps */
        dom.awaitElementPresent("[bundle-id='com.atlassian.confluence.plugins.confluence-questions']")
        dom.click("#setup-next-button")

        /* Step 3: License */
        dom.click("#confLicenseString")
        dom.insertText("#confLicenseString", "AAABLw0ODAoPeNp9kF9PgzAUxd/7KZr4og8sg8mYS0g0QJQEmJHpky933WU2YWXpn2V8ewt1mZroW\n3tue37nnquyE7SEngYR9RfLMFqGM5qkaxpM/TuSdEID0xXsMW6w5afJThqxvVcMxJZ3ky0S1olmY\nt/wI8ZaGiTPRrIPUJiCxnhw8aahFyxIwRkKhdnpwGX/bTj3gugMykrg7b8k+xESFBqlo9Vmo5jkB\n8074RRrYccCBPuDNfpUZr9BuWpeFUoVe75Taw1ysG6gVXhOnKdxkad1VnmFP5tHt5G/IPYW/1RWc\ngeCKxiD1C41fdxvnkgicVR/FzISvxjr/oBjy8mqLLOXJH8oSOtGbzbg4BmQFC+r2sKa1qBdkl4Pn\nVBXys37kmZHaM1IJJej6+YTcDek9jAsAhQMjndzQwNXokcsfeEbtiQJn5ZfSwIUaMVmEklmaIX9V\n0zou8i5649ihAg=X02f7")
        dom.click("#setupTypeCustom")

        /* Step 4: Cluster configuration */
        dom.insertText("#clusterName", "confluence-cluster")
        dom.insertText("#clusterHome", "/confluence-shared-home")
        dom.click("#cluster-auto-address")
        dom.insertText("#clusterAddressString", "230.0.0.1")
        dom.click("[name='newCluster']")

        // Step 5: DB config */
        dom.insertText("#dbConfigInfo-hostname", "confluence-cluster-6153-db")
        dom.insertText("#dbConfigInfo-port", "5432")
        dom.insertText("#dbConfigInfo-databaseName", "confluence")
        dom.insertText("#dbConfigInfo-username", "confluence")
        dom.insertText("#dbConfigInfo-password", "confluence")
        dom.click("#testConnection")

        dom.awaitSeconds(2)
        dom.awaitElementVisible("#setupdb-successMessage") // not sure if this is working
        dom.click("#setup-next-button")

        /* This takes a few minutes! Make sure the next step has a wait value! */
        log.info("Database setup in progress. This takes a while. Grab some coffee and run stage 2 afterwards\n")
        shot()
    }

}