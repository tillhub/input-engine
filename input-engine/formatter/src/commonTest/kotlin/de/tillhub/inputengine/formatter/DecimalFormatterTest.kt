package de.tillhub.inputengine.formatter

import kotlin.test.Test
import kotlin.test.assertTrue

class DecimalFormatterTest {

    @Test
    fun hasValidDecimalSeparator() {
        val sep = DecimalFormatter.decimalSeparator
        assertTrue(sep == '.' || sep == ',', "Unexpected decimal separator: $sep")
    }

    @Test
    fun hasValidGroupingSeparator() {
        val sep = DecimalFormatter.groupingSeparator
        assertTrue(sep == ',' || sep == '.' || sep == ' ', "Unexpected grouping separator: $sep")
    }
}
