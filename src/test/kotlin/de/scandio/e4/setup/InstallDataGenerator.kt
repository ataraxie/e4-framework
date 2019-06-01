package de.scandio.e4.setup

import org.junit.Test

class InstallDataGenerator : SetupBaseTest() {

    val JAR_FILE_PATH = "/tmp/e4/data-generator-LATEST.jar"

    @Test
    fun test() {
        super.installPlugin(JAR_FILE_PATH)
    }

}