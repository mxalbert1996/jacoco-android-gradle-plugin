package com.mxalbert.gradle.jacoco.android

import org.gradle.api.internal.file.collections.DefaultConfigurableFileTree
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class JacocoAndroidPluginTest {

    private lateinit var project: ProjectInternal

    @BeforeEach
    fun setup() {
        project = ProjectBuilder.builder()
            .withProjectDir(File("src/test/project"))
            .build() as ProjectInternal
        JacocoAndroidUnitTestReportExtension.defaultExcludesFactory = { listOf("default exclude") }
    }

    @Test
    fun `should throw if android plugin not applied`() {
        assertThrows<PluginApplicationException> {
            project.applyPlugin()
        }
    }

    @Test
    fun `should not create jacocoTestReport task if there is one already`() {
        val jacocoTestReportTask = project.task("jacocoTestReport")
        project.configureAndroidLibraryAndApplyPlugin()
        assertEquals(project.tasks.getByName("jacocoTestReport"), jacocoTestReportTask)
    }

    @Test
    fun `should add JacocoReport tasks for each variant`() {
        project.configureAndroidLibraryAndApplyPlugin()
        project.evaluate()

        assertNotNull(project.tasks.findByName("jacocoTestPaidDebugUnitTestReport"))
        assertNotNull(project.tasks.findByName("jacocoTestFreeDebugUnitTestReport"))
        assertNotNull(project.tasks.findByName("jacocoTestPaidDebugProguardUnitTestReport"))
        assertNotNull(project.tasks.findByName("jacocoTestFreeDebugProguardUnitTestReport"))
        assertNotNull(project.tasks.findByName("jacocoTestPaidReleaseUnitTestReport"))
        assertNotNull(project.tasks.findByName("jacocoTestFreeReleaseUnitTestReport"))
        assertNotNull(project.tasks.findByName("jacocoTestReport"))
    }

    @Test
    fun `should use default excludes`() {
        project.configureAndroidLibraryAndApplyPlugin()
        project.evaluate()
        assertAllJacocoReportTasksExclude("default exclude")
    }

    @Test
    fun `should use extension's excludes`() {
        project.configureAndroidLibraryAndApplyPlugin()
        project.jacocoAndroidUnitTestReport {
            excludes.set(listOf("some exclude"))
        }
        project.evaluate()
        assertAllJacocoReportTasksExclude("some exclude")
    }

    @Test
    fun `should merge default and extension's excludes`() {
        project.configureAndroidLibraryAndApplyPlugin()
        project.jacocoAndroidUnitTestReport {
            excludes.add("some exclude")
        }
        project.evaluate()
        assertAllJacocoReportTasksExclude("default exclude", "some exclude")
    }

    @Test
    fun `should use extension's report configuration`() {
        project.configureAndroidLibraryAndApplyPlugin()
        project.jacocoAndroidUnitTestReport {
            csv.required.set(true)
            html.required.set(true)
            xml.required.set(true)
        }
        project.evaluate()
        eachJacocoReportTask {
            assertTrue(it.reports.csv.required.get())
            assertTrue(it.reports.html.required.get())
            assertTrue(it.reports.xml.required.get())
        }
    }

    @Test
    fun `should apply which reports to build by default`() {
        project.configureAndroidLibraryAndApplyPlugin()
        project.evaluate()
        eachJacocoReportTask {
            assertFalse(it.reports.csv.required.get())
            assertTrue(it.reports.html.required.get())
            assertTrue(it.reports.xml.required.get())
        }
    }

    @Test
    fun `should add kotlin class directories if plugin added`() {
        project.configureAndroidLibraryAndApplyPlugin()
        project.plugins.apply("kotlin-android")
        project.evaluate()
        eachJacocoReportTask { jacocoReport ->
            val fileTree = jacocoReport.classDirectories.asFileTree
            assertTrue(fileTree.all { it.path.contains("/tmp/kotlin-classes/") })
        }
    }

    private fun eachJacocoReportTask(block: (JacocoReport) -> Unit) {
        project.tasks.withType(JacocoReport::class.java).all(block)
    }

    private fun assertAllJacocoReportTasksExclude(vararg expected: String) {
        eachJacocoReportTask { jacocoReport ->
            val excludes = jacocoReport.classDirectories.from
                .flatMap { (it as DefaultConfigurableFileTree).excludes }
            assertIterableEquals(excludes, expected.toList())
        }
    }
}
