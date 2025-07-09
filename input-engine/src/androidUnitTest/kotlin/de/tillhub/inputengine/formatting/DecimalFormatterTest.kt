package de.tillhub.inputengine.formatting

import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

class DecimalFormatterTest {

    @Test
    fun decimalFormatterGermanTest() = runTest {
        val formatterDe = DecimalFormatter.apply {
            locale = Locale.GERMAN
        }

        assertEquals(',', formatterDe.decimalSeparator)
        assertEquals('.', formatterDe.groupingSeparator)
    }

    @Test
    fun decimalFormatterUsTest() = runTest {
        val formatterUs = DecimalFormatter.apply {
            locale = Locale.US
        }

        assertEquals('.', formatterUs.decimalSeparator)
        assertEquals(',', formatterUs.groupingSeparator)
    }
}