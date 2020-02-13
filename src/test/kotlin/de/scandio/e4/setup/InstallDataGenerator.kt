package de.scandio.e4.setup

import org.junit.Test

class InstallDataGenerator : SetupBaseTest() {

    val JAR_FILE_PATH = "$IN_DIR/$DATA_GENERATOR_JAR_FILENAME"

    @Test
    fun test() {
        webConfluence.installPlugin(JAR_FILE_PATH)
    }

}