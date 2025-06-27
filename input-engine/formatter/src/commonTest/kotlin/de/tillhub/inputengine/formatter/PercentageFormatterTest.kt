package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.formatter.MoneyFormatterTest.Companion.LOCALE
import kotlin.test.Test
import kotlin.test.assertEquals

class PercentageFormatterTest {

    @Test
    fun testFormatInt() {
        assertEquals("0 %", PercentageFormatter.format(PercentIO.ZERO, locale = LOCALE))
        assertEquals("7 %", PercentageFormatter.format(PercentIO.of(7), locale = LOCALE))
        assertEquals("56 %", PercentageFormatter.format(PercentIO.of(56), locale = LOCALE))
        assertEquals("63 %", PercentageFormatter.format(PercentIO.of(63), locale = LOCALE))
        assertEquals("100 %", PercentageFormatter.format(PercentIO.WHOLE, locale = LOCALE))
    }

    @Test
    fun testFormatDouble() {
        assertEquals("7,01 %", PercentageFormatter.format(PercentIO.of(7.01), locale = LOCALE))
        assertEquals("34 %", PercentageFormatter.format(PercentIO.of(34.0), locale = LOCALE))
        assertEquals("34,0 %", PercentageFormatter.format(PercentIO.of(34.0), minimumFractionDigits = 1, locale = LOCALE))
        assertEquals("99,99 %", PercentageFormatter.format(PercentIO.of(99.99), locale = LOCALE))
    }
}
