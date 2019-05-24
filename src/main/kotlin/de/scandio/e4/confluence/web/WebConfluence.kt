package de.scandio.e4.confluence.web

import de.scandio.atlassian.it.pocketquery.helpers.DomHelper
import de.scandio.e4.worker.interfaces.WebClient
import de.scandio.e4.worker.util.WorkerUtils
import org.apache.commons.io.FileUtils
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URI
import java.net.URLEncoder

class WebConfluence(
        val driver: WebDriver,
        val base: URI,
        val screenshotDir: String,
        val username: String,
        val password: String
): WebClient {

    private val log = LoggerFactory.getLogger(javaClass)
    private val dom: DomHelper = DomHelper(this)

    override fun getWebDriver(): WebDriver {
        return this.driver
    }

    override fun quit() {
        this.driver.quit()
    }

    fun getDomHelper(): DomHelper {
        return this.dom
    }

    fun login() {
        // PhantomJS: about:blank, Chrome: data:,
        if (driver.currentUrl.equals("about:blank") || driver.currentUrl.equals("data:,")) { // only login once!
            navigateTo("login.action")
            dom.awaitElementPresent("form[name='loginform']", 10)
            dom.insertText("#os_username", this.username)
            dom.insertText("#os_password", this.password)
            dom.click("#loginButton")
            dom.awaitElementPresent(".pagebody", 10)
        }
    }

    fun navigateTo(path: String) {
        log.info("[SELENIUM] Navigating to {{}} with current URL {{}}", path, driver.currentUrl)
        if (!driver.currentUrl.endsWith(path)) {
            driver.navigate().to(base.resolve(path).toURL())
        } else {
            log.info("[SELENIUM] Already on page")
        }

    }

    fun takeScreenshot(screenshotName: String): String {
        val ts = driver as TakesScreenshot
        val source: File = ts.getScreenshotAs(OutputType.FILE)
        val dest = "$screenshotDir/${WorkerUtils.getRuntimeName()}-$screenshotName.png"
        log.info("[SCREENSHOT] {{}}", dest)
        System.out.println(dest)
        val destination = File(dest)
        FileUtils.copyFile(source, destination)
        return dest
    }

    fun dumpHtml(dumpName: String): String {
        val dest = "$screenshotDir/${WorkerUtils.getRuntimeName()}-$dumpName.html"
        FileUtils.writeStringToFile(File(dest), driver.pageSource, "UTF-8", false);
        System.out.println(dest)
        return dest
    }

    fun goToSpaceHomepage(spaceKey: String) {
        navigateTo("display/$spaceKey")
        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]")
    }

    fun goToPage(spaceKey: String, pageTitle: String) {
        val encodedPageTitle = URLEncoder.encode(pageTitle, "UTF-8")
        navigateTo("display/$spaceKey/$encodedPageTitle")
        dom.awaitElementPresent("#main-content")
    }

    fun goToEditPage() {
        dom.awaitElementClickable("#editPageLink")
        dom.click("#editPageLink")
        dom.awaitElementPresent("#inviteToEditLink", 30)
    }

    fun goToBlogpost(spaceKey: String, blogpostTitle: String, blogpostCreationDate: String) {
        val encodedTitle = URLEncoder.encode(blogpostTitle, "UTF-8")
        navigateTo("display/$spaceKey/$blogpostCreationDate/$encodedTitle")
    }

    fun insertMacro(macroId: String, macroSearchTerm: String) {
        dom.click("#rte-button-insert")
        dom.click("#rte-insert-macro")
        dom.insertText("#macro-browser-search", macroSearchTerm)
        dom.click("#macro-$macroId")
        dom.click("#macro-details-page button.ok", 5)
        dom.awaitElementClickable("#rte-button-publish")
//        dom.awaitElementPresent("#macro-$macroId")
//        dom.click("#macro-$macroId")
    }

    fun savePage() {
        dom.click("#rte-button-publish")
        dom.awaitElementPresent("#main-content")
    }

    fun createDefaultPage(spaceKey: String, pageTitle: String) {
        val loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Integer eget aliquet nibh praesent. Platea dictumst quisque sagittis purus sit amet volutpat consequat mauris. Montes nascetur ridiculus mus mauris vitae ultricies leo integer. In fermentum posuere urna nec. Viverra vitae congue eu consequat ac felis. Sed egestas egestas fringilla phasellus faucibus scelerisque eleifend donec pretium. Non diam phasellus vestibulum lorem sed risus ultricies. Amet tellus cras adipiscing enim eu turpis egestas pretium. A pellentesque sit amet porttitor eget dolor morbi. Integer quis auctor elit sed vulputate mi sit amet. Leo in vitae turpis massa sed elementum tempus egestas. Non odio euismod lacinia at quis risus sed vulputate odio. Nunc scelerisque viverra mauris in. Tortor at risus viverra adipiscing at. Bibendum at varius vel pharetra vel turpis."
        navigateTo("pages/createpage.action?spaceKey=$spaceKey")
        dom.awaitElementPresent("#wysiwyg")
        dom.click("#content-title-div")
        dom.insertText("#content-title", pageTitle)
        dom.click("#wysiwygTextarea_ifr")
        dom.insertTextTinyMce("<h1>Lorem Ipsum</h1><p>$loremIpsum</p>")
        dom.click("#rte-button-publish")
        dom.awaitElementPresent("#main-content")

        //        webConfluence.takeScreenshot("create-page-4")
//        dom.awaitElementPresent("#macro-browser-dialog[aria-hidden]")
//        webConfluence.takeScreenshot("create-page-5")
//        dom.insertText("#macro-browser-search", this.contentMacro)
//        dom.awaitElementPresent("#macro-info")
//        webConfluence.takeScreenshot("create-page-6")
//        dom.click("#macro-info")
//        dom.awaitElementPresent("#macro-param-title")
//        dom.insertText("#macro-param-title", this.pageTitle)
//        webConfluence.takeScreenshot("create-page-7")
//        dom.click("#macro-details-page button.ok")
//        dom.await(2000) // TODO: condition!
//        webConfluence.takeScreenshot("create-page-8")
//        dom.click("#rte-button-publish")
//        dom.awaitElementPresent(".space-logo[data-key=\"$spaceKey\"]")
//        webConfluence.takeScreenshot("create-page-9")
    }

}