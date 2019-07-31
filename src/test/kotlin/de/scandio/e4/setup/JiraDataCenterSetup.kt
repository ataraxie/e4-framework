package de.scandio.e4.setup

import de.scandio.e4.BaseSeleniumTest
import de.scandio.e4.clients.web.WebJira
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory


open class JiraDataCenterSetup : BaseSeleniumTest() {

    private val log = LoggerFactory.getLogger(javaClass)

    private val webJira: WebJira = webClient() as WebJira

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
//            webJira.navigateToBaseUrl()
//            dom.awaitElementPresent("input[name='baseURL']")
//            dom.click("#jira-setupwizard-submit")
//            log.info("Entering Jira license")
//            dom.insertText("#licenseKey", E4_APPLICATION_LICENSE)
//            dom.click("#importLicenseForm .aui-button-primary")
//            log.info("Waiting for 5 minutes for setup to complete")
//            dom.awaitMinutes(1)
//            webJira.takeScreenshot("wait-1")
//            log.info("Waiting for 4 minutes for setup to complete")
//            dom.awaitMinutes(1)
//            webJira.takeScreenshot("wait-2")
//            log.info("Waiting for 3 minutes for setup to complete")
//            dom.awaitMinutes(1)
//            webJira.takeScreenshot("wait-3")
//            log.info("Waiting for 2 minutes for setup to complete")
//            dom.awaitMinutes(1)
//            webJira.takeScreenshot("wait-4")
//            log.info("Waiting for 1 minute for setup to complete")
//            dom.awaitMinutes(1)
//            webJira.takeScreenshot("after-wait")
//            refreshWebClient()
            webJira.navigateTo("/secure/SetupAdminAccount!default.jspa")
//            dom.awaitElementPresent("input[name='fullname']")
//            dom.insertText("input[name='fullname']", "Administrator")
//            dom.insertText("input[name='email']", "admin@example.com")
//            dom.insertText("input[name='username']", USERNAME)
//            dom.insertText("input[name='password']", PASSWORD)
//            dom.insertText("input[name='confirm']", PASSWORD)
//            dom.click("#jira-setupwizard-submit")
            dom.awaitElementPresent("#jira-setupwizard-email-notifications-disabled")
            webJira.takeScreenshot("after-admin-account")
            dom.click("#jira-setupwizard-submit")
            dom.awaitElementPresent("#choose-language-form")
            webJira.takeScreenshot("language-form")
            dom.click("#next")
            dom.click(".avatar-picker-done")
            dom.click("#emptyProject")
            dom.click(".create-project-dialog-create-button.pt-submit-button")
            dom.click(".template-info-dialog-create-button.pt-submit-button")
            dom.insertText("#name", "TEST")
            dom.insertText("#key", "TEST")
            dom.click("add-project-dialog-create-button.pt-submit-button")
            dom.awaitSeconds(40)
            webJira.takeScreenshot("should-show-start-board")
        } catch (e: Exception) {
            shot()
            dump()
            throw e
        } finally {
            webJira.quit()
        }
    }

}