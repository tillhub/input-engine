package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.PercentIO
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSLocale
import kotlin.test.Test
import kotlin.test.assertEquals

class PercentageFormatterTest {

    @Test
    fun percentageFormatterTest() = runTest {
        val formatterDe = PercentageFormatter(NSLocale(localeIdentifier = "de_DE"))
        val formatterUs = PercentageFormatter(NSLocale(localeIdentifier = "en_US"))

        assertEquals("56 %", formatterDe.format(PercentIO.of(56)))
        assertEquals("5,6 %", formatterDe.format(PercentIO.of(5.6)))
        assertEquals("5.6%", formatterUs.format(PercentIO.of(5.6)))
    }
}