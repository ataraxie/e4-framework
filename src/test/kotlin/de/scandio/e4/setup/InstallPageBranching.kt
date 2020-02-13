package de.scandio.e4.setup

import org.junit.Test
import kotlin.test.assertTrue

class InstallPageBranching : SetupBaseTest() {

    val PB_JAR_FILE_PATH = "/tmp/e4/in/page-branching-1.2.0.jar"

    val ROW_SELECTOR = ".upm-plugin[data-key='de.scandio.confluence.plugins.page-branching']"
    val LICENSE_SELECTOR = "$ROW_SELECTOR textarea.edit-license-key"
    val LICENSE = "AAABOA0ODAoPeNqVkV9PwjAUxd/7KZr4vGUbAZSkiToWNWFABH3y5dLdjSalW247At/ewpgaow88NOmfc8/9ndubvDY8hyNPIh7fTpJkEkU8na79Ob5j83a3QVqUbxbJiiBmU7SSVONUbcQSKuSPBEZulal4WRNPa1PqFo3EjwnP9qBbOElZSnjeTMGhODkH0TBIYub1DqSbww5FiVodwopaU9xbCaZQdVhgL8lyUPo/zXcn4ahFppVEY/HdQ5/uEuaLjUPjUTE7NIqOP0BGQRKxAsPeT35lCBvdVsrYsPFJg02fNPQ4ao9dqwVVYJTtmq86C/602zyzVTYXfgWzeDAajMfDMZt1WH8TXB7XxwbP40gXeZ69pi8Ps+vgVg7IIYkStMXrStHPiBpS9pJt2ZLcgsXfv/YJlTfDSTAsAhQ8YDyCfUAyEm1uFV0+INy9Ywp3YAIUTk/kpoQImX1esfH2Zp08B6IiGnQ=X02fj"

    @Test
    fun test() {
        try {
            webConfluence.login()
            webConfluence.authenticateAdmin()
            webConfluence.installPlugin(PB_JAR_FILE_PATH)
            dom.click("#upm-plugin-status-dialog .cancel")
//            dom.click("$ROW_SELECTOR .upm-plugin-license-edit")
//            dom.clearText(LICENSE_SELECTOR)
            dom.insertText(LICENSE_SELECTOR, LICENSE)
            dom.awaitSeconds(5)
            dom.click("$ROW_SELECTOR .submit-license")
            dom.awaitSeconds(5)

            assertTrue(dom.isElementPresent("#upm-plugin-status-dialog") || dom.isElementPresent(".upm-plugin-license-edit"))
        } catch (e: Exception) {
            shot()
        }
    }

}