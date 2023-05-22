package com.mxalbert.gradle.jacoco.android

import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Project

fun Project.applyPlugin() {
    plugins.apply(JacocoAndroidPlugin::class.java)
}

fun Project.configureAndroidLibraryAndApplyPlugin() {
    plugins.apply(LibraryPlugin::class.java)
    extensions.configure(LibraryExtension::class.java) {
        with(it) {
            namespace = "test"
            compileSdk = 33

            defaultConfig {
                minSdk = 21
            }

            buildTypes.create("debugProguard")

            flavorDimensions += "default"
            productFlavors {
                create("free")
                create("paid")
            }
        }
    }
    applyPlugin()
}

fun Project.jacocoAndroidUnitTestReport(block: JacocoAndroidUnitTestReportExtension.() -> Unit) {
    extensions.configure(JacocoAndroidUnitTestReportExtension::class.java, block)
}
