package com.mxalbert.gradle.jacoco.android

import java.util.Collections

open class JacocoAndroidUnitTestReportExtension(var excludes: Collection<String>) {

    val csv: ReportConfiguration = ReportConfiguration(false)
    val html: ReportConfiguration = ReportConfiguration(true)
    val xml: ReportConfiguration = ReportConfiguration(true)
    var destination: String? = null

    companion object {

        @JvmStatic
        val androidDataBindingExcludes: Collection<String> = Collections.unmodifiableList(
            listOf(
                "android/databinding/**/*.class",
                "**/android/databinding/*Binding.class",
                "**/BR.*"
            )
        )

        @JvmStatic
        val androidExcludes: Collection<String> = Collections.unmodifiableList(
            listOf(
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*"
            )
        )

        @JvmStatic
        val butterKnifeExcludes: Collection<String> = Collections.unmodifiableList(
            listOf(
                "**/*\$ViewInjector*.*",
                "**/*\$ViewBinder*.*"
            )
        )

        @JvmStatic
        val dagger2Excludes: Collection<String> = Collections.unmodifiableList(
            listOf(
                "**/*_MembersInjector.class",
                "**/Dagger*Component.class",
                "**/Dagger*Component\$Builder.class",
                "**/*Module_*Factory.class"
            )
        )

        @JvmStatic
        val defaultExcludes: Collection<String> = Collections.unmodifiableList(
            androidDataBindingExcludes + androidExcludes + butterKnifeExcludes + dagger2Excludes
        )

        internal var defaultExcludesFactory: () -> Collection<String> = ::defaultExcludes
    }
}
