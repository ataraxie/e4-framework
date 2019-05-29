package de.scandio.atlassian.it.pocketquery.helpers

import de.scandio.e4.enjoy.wait
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import java.time.Duration

class DomHelper(
        val driver: WebDriver,
        val defaultDuration: Long = 20,
        val defaultWaitTillPresent: Long = 1
) {

    fun clickCreateSpace() {
        val js = driver as JavascriptExecutor
        js.executeScript("Confluence.SpaceBlueprint.Dialog.launch();")
    }

    fun awaitNoClass(selector: String, className: String, duration: Long = this.defaultDuration) {
        awaitElementPresent("$selector:not(.$className)",duration)
    }

    fun awaitClass(selector: String, className: String, duration: Long = this.defaultDuration) {
        awaitElementPresent("$selector.$className", duration)
    }

    fun awaitAttributeNotPresent(selector: String, attrName: String) {
        wait(ExpectedConditions.not(ExpectedConditions.attributeToBeNotEmpty(findElement(selector), attrName)))
    }

    fun awaitAttribute(selector: String, attrName: String, attrValue: String) {
        wait(ExpectedConditions.attributeContains(findElement(selector), attrName, attrValue))
    }

    fun setSelectedOption(selector: String, value: String) {
        val datasourceSelect = Select(findElement(selector))
        datasourceSelect.selectByValue(value)
    }

    fun insertTextCodeMirror(value: String) {
        val js = driver as JavascriptExecutor
        js.executeScript("arguments[0].CodeMirror.setValue(\"$value\");", findElement(".CodeMirror"));
    }

    fun awaitElementInsivible(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.invisibilityOf(findElement(selector)), duration)
    }

    fun awaitElementPresent(selector: String, duration: Long = 60) {
        wait(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)), duration)
    }

    fun awaitElementNotPresent(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector))), duration)
    }

    fun awaitElementClickable(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)), duration)
    }

    fun awaitElementClickable(element: WebElement, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.elementToBeClickable(element), duration)
    }

    fun awaitElementNotClickable(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(By.cssSelector(selector))), duration)
    }

    fun awaitHasText(selector: String, text: String) {
        wait(ExpectedConditions.textToBePresentInElement(findElement(selector), text))
    }

    fun awaitHasValue(selector: String, value: String) {
        wait(ExpectedConditions.attributeContains(By.cssSelector(selector), "value", value))
    }

    fun insertText(selector: String, text: String, awaitClickableSeconds: Long = this.defaultWaitTillPresent) {
        awaitElementPresent(selector, awaitClickableSeconds)
        findElement(selector).sendKeys(text)
        await(10)
    }

    fun insertTextTinyMce(text: String) {
        val js = driver as JavascriptExecutor
        js.executeScript("tinyMCE.activeEditor.setContent('${text.replace("'", "\\'")}')")
    }

    fun clearText(selector: String) {
        findElement(selector).clear()
        await(10)
    }

    fun click(selector: String, awaitClickableSeconds: Long = this.defaultWaitTillPresent) {
        awaitElementClickable(selector, awaitClickableSeconds)
        findElement(selector).click()
    }

    fun click(element: WebElement, awaitClickableSeconds: Long = this.defaultWaitTillPresent) {
        awaitElementClickable(element, awaitClickableSeconds)
        element.click()
    }

    fun findElement(cssSelector: String): WebElement {
        return driver.findElement(By.cssSelector(cssSelector))
    }

    fun findElements(cssSelector: String): List<WebElement> {
        return driver.findElements(By.cssSelector(cssSelector))
    }

    fun await(ms: Long) {
        Thread.sleep(ms)
    }

    fun <T> wait(condition: ExpectedCondition<T>, duration: Long = this.defaultDuration) {
        await(10)
        driver.wait(
                Duration.ofSeconds(duration),
                condition
        )
        await(10)
    }
}