package de.scandio.e4.setup

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory


open class ConfluenceDataCenterSetup : SetupBaseTest() {

    private val log = LoggerFactory.getLogger(javaClass)

    private val E4_LICENSE_CONF = System.getenv("E4_LICENSE_CONF")
    private val E4_LICENSE_PAGEBRANCHING = System.getenv("E4_LICENSE_PAGEBRANCHING")

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
            webConfluence.takeScreenshot("db-ready")
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

        webConfluence.login()
        webConfluence.authenticateAdmin()
        webConfluence.installPlugin(PB_JAR_FILE_PATH)
        dom.click("#upm-plugin-status-dialog .cancel")
        dom.insertText(LICENSE_SELECTOR, E4_LICENSE_PAGEBRANCHING)
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
        dom.insertText("#confLicenseString", E4_LICENSE_CONF)
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
        log.info("Database setup in progress. This takes a while. Grab some coffee... :)")
        shot()
    }

    fun setupStep2() {
        webConfluence.navigateTo("setup/setupdata-start.action")
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
        dom.awaitSeconds(10)
        webConfluence.navigateTo("logout.action")
//        dom.click("#onboarding-skip-editor-tutorial")
//        dom.click("#editor-precursor > .cell")
//        dom.click("#content-title")
//        dom.insertText("#content-title", "Test Page")
//        dom.click("#rte-button-publish")
//        dom.awaitElementClickable("#main-content")

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
                log.info("+++++++++++++++++++++++++++++++++++++++++!")
                log.info("+++++++++++ Done with DB Setup ++++++++++!")
                log.info("+++++++++++++++++++++++++++++++++++++++++!")
                log.info("But waiting for another safety minute because the setup wizard is buggy...")
                dom.awaitMinutes(1)
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