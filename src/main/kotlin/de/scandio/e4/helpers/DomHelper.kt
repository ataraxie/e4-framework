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
import java.net.URLEncoder
import java.time.Duration
import org.openqa.selenium.interactions.Actions


class DomHelper(
        val driver: WebDriver,
        var defaultDuration: Long = 30,
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

    fun removeElementWithJQuery(selector: String) {
        val js = driver as JavascriptExecutor
        js.executeScript("$(\".aui-blanket\").remove()")
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

    fun setSelectedOptionByValue(selector: String, value: String) {
        val select = Select(findElement(selector))
        select.selectByValue(value)
    }

    fun setSelectedOptionByText(selector: String, text: String) {
        val select = Select(findElement(selector))
        select.selectByVisibleText(text)
    }

    fun setSelect2OptionByText(selector: String, text: String) {
        setSelectedOptionByText(selector, text)
        awaitMilliseconds(50)
    }

    fun executeScript(script: String, container: WebElement? = null): Any? {
        val js = driver as JavascriptExecutor
        return if (container == null) {
            js.executeScript(script)
        } else {
            js.executeScript(script, container)
        }
    }

    fun insertTextCodeMirror(value: String) {
        val js = driver as JavascriptExecutor
        js.executeScript("arguments[0].CodeMirror.setValue(\"$value\");", findElement(".CodeMirror"));
    }

    fun awaitElementInsivible(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(selector)), duration)
    }

    fun awaitElementVisible(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)), duration)
    }

    fun awaitElementPresent(selector: String, duration: Long = this.defaultDuration) {
        log.debug("Waiting for element {{}} to be present for {{}} seconds", selector, duration)
        wait(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)), duration)
    }

    fun awaitElementNotPresent(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.not(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector))), duration)
    }

    fun awaitElementClickable(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)), duration)
        awaitMilliseconds(10)
    }

    fun awaitElementClickable(element: WebElement, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.elementToBeClickable(element), duration)
        awaitMilliseconds(10)
    }

    fun awaitElementNotClickable(selector: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(By.cssSelector(selector))), duration)
    }

    fun awaitHasText(selector: String, text: String, duration: Long = this.defaultDuration) {
        wait(ExpectedConditions.textToBePresentInElement(findElement(selector), text), duration)
    }

    fun awaitHasValue(selector: String, value: String) {
        wait(ExpectedConditions.attributeContains(By.cssSelector(selector), "value", value))
    }

    fun awaitSelected(selector: String) {
        wait(ExpectedConditions.elementToBeSelected(By.cssSelector(selector)))
    }

    fun isScrollbarVisible(selector: String): Boolean {
        val elem = findElement(selector)
        return executeScript("return arguments[0].scrollHeight > arguments[0].offsetHeight;", elem) as Boolean
    }

    fun scrollToBottom(selector: String) {
        val elem = findElement(selector)
        executeScript("arguments[0].scrollTo(0, arguments[0].scrollHeight)", elem)
    }

    fun scrollToTop(selector: String) {
        val elem = findElement(selector)
        executeScript("arguments[0].scrollTo(0, 0)", elem)
    }

    fun insertText(selector: String, text: String, clearText: Boolean = false) {
        if (this.screenshotBeforeInsert) {
            this.util.takeScreenshot(driver, "$outDir/insert-$selector.png")
        }
        awaitElementPresent(selector)
        if (clearText) {
            clearText(selector)
        }
        findElement(selector).sendKeys(text)
        awaitMilliseconds(50)
    }

    fun addTextTinyMce(html: String) {
        awaitElementPresent("#wysiwygTextarea_ifr")
        awaitMilliseconds(100)
        val js = driver as JavascriptExecutor
        val oldContent = js.executeScript("return tinyMCE.activeEditor.getContent()")
        val newContent = "$oldContent$html".replace("'", "\\'")
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
        log.debug("Click {{}} wait {{}}sec", selector, awaitClickableSeconds)
        if (this.screenshotBeforeClick) {
            val safeSelector = URLEncoder.encode("$selector", "UTF-8")
            this.util.takeScreenshot(driver, "$outDir/click-$safeSelector.png")
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

    fun clickAll(selector: String) {
        for (element in findElements(selector)) {
            click(element)
            awaitMilliseconds(50)
        }
    }

    fun unselectAll(selector: String) {
        for (element in findElements(selector)) {
            if (element.isSelected) {
                element.click()
            }
        }
    }

    fun insertHtmlInEditor(containerSelector: String, html: String) {
        driver.switchTo().frame("wysiwygTextarea_ifr")
        executeScript("document.querySelectorAll('$containerSelector').innerHTML = 'TEST';")
        driver.switchTo().parentFrame()
    }

    fun setFile(inputSelector: String, absolutePath: String) {
        findElement(inputSelector).sendKeys(absolutePath)
    }

    fun expectElementPresent(selector: String) {
        findElement(selector)
    }

    fun expectElementNotPresent(selector: String) { // FIXME: this is slow because findElement is waiting! Should be improved!
        var elementFound = false
        try {
            findElement(selector)
            elementFound = true
        } catch (e: Exception) {
            // leave false
        }

        if (elementFound) {
            throw Exception("Found element that was not expected")
        }
    }

    fun hoverOverElement(selector: String) {
        val builder = Actions(driver)
        builder.moveToElement(findElement(selector)).perform()
    }

    fun expectElementDisplayed(selector: String) {
        assert(!findElement(selector).getCssValue("display").equals("none"))
    }

    fun expectElementNotDisplayed(selector: String) {
        assert(findElement(selector).getCssValue("display").equals("none"))
    }

}