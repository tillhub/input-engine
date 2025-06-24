package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.PercentIO
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

class PercentageFormatterAndroidTest {

    @Test
    fun testFormatInt() {
        assertEquals("0 %", PercentageFormatter.format(PercentIO.ZERO, locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("7 %", PercentageFormatter.format(PercentIO.of(7), locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("56 %", PercentageFormatter.format(PercentIO.of(56), locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("63 %", PercentageFormatter.format(PercentIO.of(63), locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("100 %", PercentageFormatter.format(PercentIO.WHOLE, locale = Locale.GERMAN.toLanguageTag()))
    }

    @Test
    fun testFormatDouble() {
        assertEquals("7,01 %", PercentageFormatter.format(PercentIO.of(7.01), locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("34 %", PercentageFormatter.format(PercentIO.of(34.0), locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("34,0 %", PercentageFormatter.format(PercentIO.of(34.0), minimumFractionDigits = 1, locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("99,99 %", PercentageFormatter.format(PercentIO.of(99.99), locale = Locale.GERMAN.toLanguageTag()))
    }
}
