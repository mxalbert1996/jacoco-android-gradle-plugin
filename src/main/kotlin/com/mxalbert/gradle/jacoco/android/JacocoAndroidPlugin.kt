package com.mxalbert.gradle.jacoco.android

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

class JacocoAndroidPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.checkAndroidPlugin()
        project.plugins.apply(JacocoPlugin::class.java)
        val extension = project.extensions.create(
            "jacocoAndroidUnitTestReport",
            JacocoAndroidUnitTestReportExtension::class.java,
            JacocoAndroidUnitTestReportExtension.defaultExcludesFactory()
        )
        val jacocoTestReportTask = project.findOrCreateJacocoTestReportTask()

        project.extensions.configure(AndroidComponentsExtension::class.java) { androidComponents ->
            androidComponents.onVariants { variant ->
                val reportTask = project.createReportTask(extension, variant)
                jacocoTestReportTask.dependsOn(reportTask)
            }
        }
    }

    companion object {
        private val logger = Logging.getLogger(JacocoAndroidPlugin::class.java)

        private val ANDROID_PLUGINS = listOf(
            "com.android.application",
            "com.android.library",
            "com.android.dynamic-feature"
        )

        private fun Project.checkAndroidPlugin() {
            check(ANDROID_PLUGINS.any { plugins.hasPlugin(it) }) {
                "You must apply Android plugin before jacoco-android plugin"
            }
        }

        private fun Project.findOrCreateJacocoTestReportTask(): Task =
            tasks.findByName("jacocoTestReport")
                ?: tasks.create("jacocoTestReport").apply { group = "Reporting" }

        @Suppress("UnstableApiUsage")
        private fun Project.createReportTask(
            extension: JacocoAndroidUnitTestReportExtension,
            variant: Variant
        ): TaskProvider<JacocoReport> {
            val name = variant.name
            val capitalizedName = name.replaceFirstChar { it.titlecase() }
            val javaSources = variant.sources.java?.all?.orNull
            val kotlinSources = variant.sources.kotlin?.all?.orNull

            val reportTask = tasks.register(
                "jacocoTest${capitalizedName}UnitTestReport",
                JacocoReport::class.java
            ) { reportTask ->
                val testTask = tasks.getByName("test${capitalizedName}UnitTest")
                val executionData =
                    testTask.extensions.getByType(JacocoTaskExtension::class.java).destinationFile
                reportTask.dependsOn(testTask)
                reportTask.group = "Reporting"
                reportTask.description =
                    "Generates Jacoco coverage reports for the $name variant."
                executionData?.let { reportTask.executionData.setFrom(files(it)) }
                reportTask.sourceDirectories.apply {
                    javaSources?.let { from(files(it)) }
                    kotlinSources?.let { from(files(it)) }
                }

                val javaCompile =
                    tasks.getByName("compile${capitalizedName}JavaWithJavac") as JavaCompile
                val javaClassesDir = javaCompile.destinationDirectory
                val javaTree = fileTree(javaClassesDir) { it.excludes += extension.excludes }
                reportTask.classDirectories.from(javaTree)

                if (plugins.hasPlugin("kotlin-android")) {
                    val kotlinCompile =
                        tasks.getByName("compile${capitalizedName}Kotlin") as KotlinCompile
                    val kotlinClassesDir = kotlinCompile.destinationDirectory
                    val kotlinTree =
                        fileTree(kotlinClassesDir) { it.excludes += extension.excludes }
                    reportTask.classDirectories.from(kotlinTree)
                }

                reportTask.reports { reports ->
                    reports.csv.required.set(extension.csv.isEnabled)
                    if (extension.csv.isEnabled) {
                        reports.csv.outputLocation.set(
                            extension.destination?.let { File("$it/jacoco.csv") }
                                ?: File(buildDir, "jacoco/jacoco.csv")
                        )
                    }

                    reports.html.required.set(extension.html.isEnabled)
                    if (extension.html.isEnabled) {
                        reports.html.outputLocation.set(
                            extension.destination?.let { File("$it/jacocoHtml") }
                                ?: File(buildDir, "jacoco/jacocoHtml")
                        )
                    }

                    reports.xml.required.set(extension.xml.isEnabled)
                    if (extension.xml.isEnabled) {
                        reports.xml.outputLocation.set(
                            extension.destination?.let { File("$it/jacoco.xml") }
                                ?: File(buildDir, "jacoco/jacoco.xml")
                        )
                    }
                }

                logTaskAdded(reportTask)
            }

            return reportTask
        }

        private fun logTaskAdded(reportTask: JacocoReport) {
            logger.info("Added $reportTask")
            logger.info("  executionData: ${reportTask.executionData.asPath}")
            logger.info("  sourceDirectories: ${reportTask.sourceDirectories.asPath}")
            logger.info("  csv.destination: ${reportTask.reports.csv.outputLocation.get().asFile.path}")
            logger.info("  xml.destination: ${reportTask.reports.xml.outputLocation.get().asFile.path}")
            logger.info("  html.destination: ${reportTask.reports.html.outputLocation.get().asFile.path}")
        }
    }
}
