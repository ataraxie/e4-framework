package de.scandio.e4.helpers

import de.scandio.e4.enjoy.wait
import de.scandio.e4.worker.util.Util
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.time.Duration

class DomHelper(
        val driver: WebDriver,
        var defaultDuration: Long = 20,
        var defaultWaitTillPresent: Long = 10,
        var screenshotBeforeClick: Boolean = false,
        var screenshotBeforeInsert: Boolean = false,
        var outDir: String = "/tmp",
        val util: Util = Util()
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun clickCreateSpace() {
        val js = driver as JavascriptExecutor
        js.executeScript("Confluence.SpaceBlueprint.Dialog.launch();")
    }

    fun awaitNoClass(selector: String, className: String, duration: Long = this.defaultDuration) {
        awaitElementClickable("$selector:not(.$className)",duration)
    }

    fun awaitClass(selector: String, className: String, duration: Long = this.defaultDuration) {
        awaitElementClickable("$selector.$className", duration)
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

    fun awaitElementVisible(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.visibilityOf(findElement(selector)), duration)
    }

    fun awaitElementPresent(selector: String, duration: Long = this.defaultDuration) {
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
        if (this.screenshotBeforeInsert) {
            this.util.takeScreenshot(driver, "$outDir/insert-$selector.png")
        }
        awaitElementPresent(selector, awaitClickableSeconds)
        findElement(selector).sendKeys(text)
        awaitMilliseconds(50)
    }

    fun addTextTinyMce(html: String) {
        awaitElementPresent("#wysiwygTextarea_ifr")
        awaitMilliseconds(100)
        val js = driver as JavascriptExecutor
        val oldContent = js.executeScript("return tinyMCE.activeEditor.getContent()")
        val newContent = "$oldContent${html.replace("'", "\\'")}"
        log.debug("Insert into TinyMCE. Old content {{}}; new content {{}}", oldContent, newContent)
        js.executeScript("tinyMCE.activeEditor.setContent('$newContent')")
    }

    fun insertTextTinyMce(html: String) {
        awaitElementPresent("#wysiwygTextarea_ifr")
        awaitMilliseconds(100)
        val js = driver as JavascriptExecutor
        val escapedHtml = html.replace("'", "\\'")
        log.debug("Insert into TinyMCE. New content {{}}", html)
        js.executeScript("tinyMCE.activeEditor.setContent('$escapedHtml')")
    }

    fun clearText(selector: String) {
        findElement(selector).clear()
        awaitMilliseconds(50)
    }

    fun click(selector: String, awaitClickableSeconds: Long = this.defaultWaitTillPresent) {
        if (this.screenshotBeforeClick) {
            this.util.takeScreenshot(driver, "$outDir/click-$selector.png")
        }
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

    fun awaitMilliseconds(ms: Long) {
        Thread.sleep(ms)
    }

    fun awaitSeconds(seconds: Long) {
        Thread.sleep(seconds * 1000)
    }

    fun awaitMinutes(minutes: Long) {
        Thread.sleep(minutes * 60 * 1000)
    }

    fun isElementPresent(selector: String): Boolean {
        try {
            findElement(selector)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun <T> wait(condition: ExpectedCondition<T>, duration: Long = this.defaultDuration) {
        awaitMilliseconds(10)
        driver.wait(
                Duration.ofSeconds(duration),
                condition
        )
        awaitMilliseconds(10)
    }
}