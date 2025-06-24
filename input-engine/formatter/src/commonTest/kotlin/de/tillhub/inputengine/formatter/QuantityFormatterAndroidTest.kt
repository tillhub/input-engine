package de.tillhub.inputengine.formatter

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.formatter.MoneyFormatterTest.Companion.LOCALE
import kotlin.test.Test
import kotlin.test.assertEquals

class QuantityFormatterAndroidTest {

    @Test
    fun testFormatInt() {
        assertEquals("1", QuantityFormatter.format(QuantityIO.of(1), locale = LOCALE))
        assertEquals("10,00", QuantityFormatter.format(QuantityIO.of(10), 2, locale = LOCALE))
    }

    @Test
    fun testFormatDouble() {
        assertEquals("1,23", QuantityFormatter.format(QuantityIO.of(1.23), locale = LOCALE))
        assertEquals("1", QuantityFormatter.format(QuantityIO.of(1.00), locale = LOCALE))
    }

    @Test
    fun testFormatBigDecimal() {
        assertEquals("1,23", QuantityFormatter.format(QuantityIO.of(BigDecimal.fromDouble(1.23)), locale = LOCALE))
        assertEquals("1", QuantityFormatter.format(QuantityIO.of(BigDecimal.fromDouble(1.00)), locale = LOCALE))
    }
}
