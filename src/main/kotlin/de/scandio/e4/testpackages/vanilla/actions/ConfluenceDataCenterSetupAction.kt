package de.scandio.e4.testpackages.vanilla.actions

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.worker.interfaces.Action
import de.scandio.e4.worker.interfaces.RestClient
import de.scandio.e4.worker.interfaces.WebClient
import org.slf4j.LoggerFactory
import java.util.*

class ConfluenceDataCenterSetupAction : Action() {

    private val log = LoggerFactory.getLogger(javaClass)

    private var start: Long = 0
    private var end: Long = 0

    private var screenshotCount = 0
    private var dumpCount = 0

    private var webConfluence: WebConfluence? = null

    override fun execute(webClient: WebClient, restClient: RestClient) {
        this.webConfluence = webClient as WebConfluence
        val webConfluence = this.webConfluence!!
        val dom = webConfluence.dom

        dom.defaultDuration = 40
        dom.defaultWaitTillPresent = 40
        dom.screenshotBeforeClick = true
        dom.screenshotBeforeInsert = true

        this.start = Date().time
        try {
            databaseSetup()
            setupAdminAccountAndSpace()
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
//            webConfluence.installPlugin("$DATA_GENERATOR_JAR_FILENAME")

            refreshWebClient(true, true)
//            installPageBranching()
        } catch (e: Exception) {
            shot()
            dump()
            throw e
        } finally {
            webConfluence.quit()
        }
        this.end = Date().time
    }

    private fun refreshWebClient(login: Boolean, authenticate: Boolean) {

    }

    override fun getTimeTaken(): Long {
        return this.end - this.start
    }


    fun databaseSetup() {
        webConfluence!!.navigateTo("")
        val dom = webConfluence!!.dom
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
        log.info("Database setup in progress. This takes a while. Grab some coffee... :)")
        dom.awaitMinutes(4)
        pollTillDbReady()
        shot()
    }

    fun setupAdminAccountAndSpace() {
        val webConfluence = webConfluence!!
        val dom = webConfluence.dom

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

        shot()
    }

    private fun pollTillDbReady() {
        val webConfluence = webConfluence!!
        val dom = webConfluence.dom
        val pollMax = 8
        var pollCount = 1
        while (true) {
            refreshWebClient(false, false)
            webConfluence.navigateTo("")
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

    fun shot() {
        this.screenshotCount += 1
        val path = webConfluence!!.takeScreenshot("$screenshotCount-confluence-data-center-setup.png")
        println(path)
    }

    fun dump() {
        this.dumpCount += 1
        val path = webConfluence!!.dumpHtml("$dumpCount-confluence-data-center-setup.html")
        println(path)
    }


}