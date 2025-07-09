package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.PercentIO
import kotlinx.coroutines.test.runTest
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class PercentageFormatterTest {

    @Test
    fun percentageFormatterTest() = runTest {
        val formatterDe = PercentageFormatter(Locale.GERMAN)
        val formatterUs = PercentageFormatter(Locale.US)

        assertEquals("56 %", formatterDe.format(PercentIO.of(56)))
        assertEquals("5,6 %", formatterDe.format(PercentIO.of(5.6)))
        assertEquals("5.6%", formatterUs.format(PercentIO.of(5.6)))
    }
}