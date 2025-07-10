package de.tillhub.inputengine.formatting

import platform.Foundation.NSLocale
import kotlin.test.Test
import kotlin.test.assertEquals

class DecimalFormatterTest {

    @Test
    fun decimalFormatterGermanTest() {
        val formatterDe = DecimalFormatter.apply {
            locale = NSLocale(localeIdentifier = "de_DE")
        }

        assertEquals(',', formatterDe.decimalSeparator)
        assertEquals('.', formatterDe.groupingSeparator)
    }

    @Test
    fun decimalFormatterUsTest() {
        val formatterUs = DecimalFormatter.apply {
            locale = NSLocale(localeIdentifier = "en_US")
        }

        assertEquals('.', formatterUs.decimalSeparator)
        assertEquals(',', formatterUs.groupingSeparator)
    }
}
