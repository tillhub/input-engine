package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.QuantityIO
import kotlinx.coroutines.test.runTest
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class QuantityFormatterTest {
    @Test
    fun quantityFormatterTest() = runTest {
        val formatterDe = QuantityFormatterImpl(Locale.GERMAN)
        val formatterUs = QuantityFormatterImpl(Locale.US)

        assertEquals("10", formatterDe.format(QuantityIO.of(10)))
        assertEquals("10", formatterDe.format(QuantityIO.of(10.0)))
        assertEquals("10,25", formatterDe.format(QuantityIO.of(10.25)))
        assertEquals("10.25", formatterUs.format(QuantityIO.of(10.25)))
    }
}
