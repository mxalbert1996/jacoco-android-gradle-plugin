package com.mxalbert.gradle.jacoco.android

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.util.Collections

open class JacocoAndroidUnitTestReportExtension internal constructor(
    project: Project,
    defaultExcludes: Collection<String>
) {

    val excludes: SetProperty<String> = project.objects.setProperty(String::class.java).apply {
        set(defaultExcludes)
    }

    val csv: ReportConfiguration = project.objects
        .newInstance(ReportConfiguration::class.java).apply { required.convention(false) }

    val html: ReportConfiguration = project.objects
        .newInstance(ReportConfiguration::class.java).apply { required.convention(true) }

    val xml: ReportConfiguration = project.objects
        .newInstance(ReportConfiguration::class.java).apply { required.convention(true) }

    val destination: Property<String> = project.objects.property(String::class.java)

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
