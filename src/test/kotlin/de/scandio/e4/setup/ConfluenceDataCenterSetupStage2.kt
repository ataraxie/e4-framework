package de.scandio.e4.setup

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By


/*
Search/Replace for Java export from Selenium Chrome addon:

driver.findElement\(By.id\("(.*)"\)\).sendKeys\("(.*)"\)
dom.insertText("#$1", "$2")


driver.findElement\(By.id\("(.*)"\)\)\.click\(\)
dom.click("#$1")
 */

class ConfluenceDataCenterSetupStage2 : ConfluenceDataCenterSetupStage1() {

    @Before
    override fun before() {
        super.before()
    }

    @After
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    override fun test() {
        driver.navigate().to(BASE_URL + "setup/setupdata-start.action")
        dom.awaitSeconds(3) // just wait a bit for safety

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

        shot()
    }
}