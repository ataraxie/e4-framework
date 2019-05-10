package de.scandio.atlassian.it.pocketquery.helpers

import de.scandio.e4.confluence.web.WebConfluence
import de.scandio.e4.enjoy.wait
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import java.time.Duration

class DomHelper(
        val confluence: WebConfluence
) {

    fun awaitNoClass(selector: String, className: String, duration: Long = 5) {
        awaitElementPresent("$selector:not(.$className)",duration)
    }

    fun awaitClass(selector: String, className: String, duration: Long = 5) {
        awaitElementPresent("$selector.$className", duration)
    }

    fun awaitAttributeNotPresent(selector: String, attrName: String) {
        wait(ExpectedConditions.not(ExpectedConditions.attributeToBeNotEmpty(findElement(selector), attrName)))
    }

    fun awaitAttribute(selector: String, attrName: String, attrValue: String) {
        wait(ExpectedConditions.attributeContains(findElement(selector), attrName, attrValue))
    }

    fun await(ms: Long) {
        Thread.sleep(ms)
    }

    fun setSelectedOption(selector: String, value: String) {
        val datasourceSelect = Select(findElement(selector))
        datasourceSelect.selectByValue(value)
    }

    fun insertTextCodeMirror(value: String) {
        val js = confluence.driver as JavascriptExecutor
        js.executeScript("arguments[0].CodeMirror.setValue(\"$value\");", findElement(".CodeMirror"));
    }

    fun awaitElementPresent(selector: String, duration: Long = 10) {
        wait(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)), duration)
    }

    fun awaitElementNotPresent(selector: String, duration: Long = 10) {
        wait(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector))), duration)
        Thread.sleep(100)
    }

    fun awaitHasText(selector: String, text: String) {
        wait(ExpectedConditions.textToBePresentInElement(findElement(selector), text))
    }

    fun awaitHasValue(selector: String, value: String) {
        wait(ExpectedConditions.attributeContains(By.cssSelector(selector), "value", value))
    }

    fun insertText(selector: String, text: String) {
        findElement(selector).sendKeys(text)
    }

    fun insertTextTinyMce(text: String) {
        val js = confluence.driver as JavascriptExecutor
        js.executeScript("tinyMCE.activeEditor.setContent('${text.replace("'", "\\'")}')")
    }

    fun clearText(selector: String) {
        findElement(selector).clear()
    }

    fun click(selector: String) {
        findElement(selector).click()
    }

    fun findElement(cssSelector: String): WebElement {
        return confluence.driver.findElement(By.cssSelector(cssSelector))
    }

    fun <T> wait(condition: ExpectedCondition<T>, duration: Long = 5) {
        confluence.driver.wait(
                Duration.ofSeconds(duration),
                condition
        )
    }
}