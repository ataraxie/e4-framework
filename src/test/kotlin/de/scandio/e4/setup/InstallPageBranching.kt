package de.scandio.e4.setup

import org.junit.Test
import java.util.concurrent.TimeoutException

class InstallPageBranching : SetupBaseTest() {

    val JAR_FILE_PATH = "/tmp/e4/page-branching-1.2.0.jar"

    @Test
    fun test() {
        try {
            webConfluence.installPlugin(JAR_FILE_PATH, "de.scandio.confluence.plugins.pagebranching")// TODO check key
        } catch (e: TimeoutException) {
            shot()
        }
    }

}