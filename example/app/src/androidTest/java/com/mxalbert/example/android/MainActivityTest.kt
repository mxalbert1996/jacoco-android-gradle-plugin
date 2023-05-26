package com.mxalbert.example.android

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val activityTestRule = activityScenarioRule<MainActivity>()

    @Test
    fun shouldShow42() {
        Espresso.onView(ViewMatchers.withText("42"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
