package de.scandio.e4.setup

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory


open class ConfluenceDataCenterSetup : SetupBaseTest() {

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
        try {
            setupStep1()
            dom.awaitMinutes(4)
            pollTillDbReady()
            refreshWebClient()
            setupStep2()
            refreshWebClient(true, true)

            /* Step 9: Admin config */
            webConfluence.disableMarketplaceConnectivity()
            refreshWebClient(true, true)
            webConfluence.disableSecureAdminSessions()
            refreshWebClient(true, true)
            webConfluence.disableCaptchas()
            refreshWebClient(true, true)
            webConfluence.setLogLevel("co.goodsoftware", "INFO")
            refreshWebClient(true, true)
            webConfluence.disablePlugin("com.atlassian.troubleshooting.plugin-confluence")
            refreshWebClient(true, true)
            webConfluence.disablePlugin("com.atlassian.plugins.base-hipchat-integration-plugin")
            refreshWebClient(true, true)
            webConfluence.installPlugin("$IN_DIR/$DATA_GENERATOR_JAR_FILENAME")

            refreshWebClient(true, true)
            installPageBranching()
        } catch (e: Exception) {
            shot()
            dump()
            throw e
        } finally {
            webConfluence.quit()
        }
    }

    fun installPageBranching() {
        val PB_JAR_FILE_PATH = "/tmp/e4/in/page-branching-1.2.0.jar"

        val ROW_SELECTOR = ".upm-plugin[data-key='de.scandio.confluence.plugins.page-branching']"
        val LICENSE_SELECTOR = "$ROW_SELECTOR textarea.edit-license-key"
        val LICENSE = "AAABOA0ODAoPeNqVkV9PwjAUxd/7KZr4vGUbAZSkiToWNWFABH3y5dLdjSalW247At/ewpgaow88NOmfc8/9ndubvDY8hyNPIh7fTpJkEkU8na79Ob5j83a3QVqUbxbJiiBmU7SSVONUbcQSKuSPBEZulal4WRNPa1PqFo3EjwnP9qBbOElZSnjeTMGhODkH0TBIYub1DqSbww5FiVodwopaU9xbCaZQdVhgL8lyUPo/zXcn4ahFppVEY/HdQ5/uEuaLjUPjUTE7NIqOP0BGQRKxAsPeT35lCBvdVsrYsPFJg02fNPQ4ao9dqwVVYJTtmq86C/602zyzVTYXfgWzeDAajMfDMZt1WH8TXB7XxwbP40gXeZ69pi8Ps+vgVg7IIYkStMXrStHPiBpS9pJt2ZLcgsXfv/YJlTfDSTAsAhQ8YDyCfUAyEm1uFV0+INy9Ywp3YAIUTk/kpoQImX1esfH2Zp08B6IiGnQ=X02fj"

        webConfluence.login()
        webConfluence.authenticateAdmin()
        webConfluence.installPlugin(PB_JAR_FILE_PATH)
        dom.click("#upm-plugin-status-dialog .cancel")
        dom.insertText(LICENSE_SELECTOR, LICENSE)
        dom.awaitSeconds(5)
        dom.click("$ROW_SELECTOR .submit-license")
        dom.awaitSeconds(5)
    }

    fun setupStep1() {
        driver.navigate().to(BASE_URL) // TODO use webConfluence.navigateTo
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

    fun setupStep2() {
        driver.navigate().to(BASE_URL) // TODO use webConfluence.navigateTo
        dom.awaitSeconds(3) // just wait a bit for safety

        /* Step 6: Setup data */
        dom.click("input[Value='Empty Site']")

        /* Step 7: User management */
        dom.click("#internal")

        /* Step 8: Admin account */

        dom.insertText("#fullName", "Administrator")
        dom.insertText("#email", "admin@example.com")
        dom.insertText("#password", "admin")
        dom.insertText("#confirm", "admin")
        dom.click("#setup-next-button")


        dom.click(".setup-success-button .aui-button-primary.finishAction")

        dom.insertText("#grow-intro-space-name", "TEST")
        dom.click("#grow-intro-create-space")
        dom.click("#onboarding-skip-editor-tutorial")
        dom.click("#editor-precursor > .cell")
        dom.click("#content-title")
        dom.insertText("#content-title", "Test Page")
        dom.click("#rte-button-publish")
        dom.awaitElementPresent("#main-content")

        shot()
    }

    fun setupStep3() {

    }

    private fun pollTillDbReady() {
        val pollMax = 8
        var pollCount = 1
        while (true) {
            refreshWebClient(false, false)
            driver.navigate().to(BASE_URL)
            dom.awaitSeconds(10)
            if (dom.isElementPresent("form[action='setupdata.action']")) {
                log.info("Done!")
                break
            }

            if (pollCount >= pollMax) {
                shot()
                log.warn("Waited for {} minutes. Won't wait longer.", pollMax)
                throw Exception("Waited too long")
            } else {
                log.info("Not done yet. Waiting for another minute")
                shot()
                pollCount++
                dom.awaitMinutes(1)
            }
        }
    }

}