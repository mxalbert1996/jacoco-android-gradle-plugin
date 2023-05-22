package com.mxalbert.gradle.jacoco.android

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.io.File

class PluginIntegrationTest {

    @Test
    fun buildProject() {
        val output = GradleRunner.create()
            .withProjectDir(File("src/integrationTest/project"))
            .withArguments(
                "-PpluginVersion=${System.getProperty("pluginVersion")!!}",
                "clean",
                "jacocoTestFreeDebugProguardUnitTestReport",
                "--stacktrace"
            )
            .forwardOutput()
            .build()
            .output
        assertFalse(output.contains("warning", ignoreCase = true))
        assertFalse(output.contains("error", ignoreCase = true))
    }
}
