package com.mxalbert.example.android

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberProviderTest {

    @Test
    fun shouldProvideProperNumber() {
        val numberProvider = NumberProvider()
        val number = numberProvider.provideNumber()
        assertEquals(42, number)
    }
}
