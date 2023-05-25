package com.mxalbert.gradle.jacoco.android

import org.gradle.api.provider.Property

abstract class ReportConfiguration {

    abstract val required: Property<Boolean>

    @Deprecated(
        message = "Use required.get() instead.",
        replaceWith = ReplaceWith("required.get()")
    )
    val isEnabled: Boolean
        get() = required.get()

    @Deprecated(
        message = "Use required.set() instead.",
        replaceWith = ReplaceWith("required.set(enabled)")
    )
    fun enabled(enabled: Boolean) {
        required.set(enabled)
    }
}
