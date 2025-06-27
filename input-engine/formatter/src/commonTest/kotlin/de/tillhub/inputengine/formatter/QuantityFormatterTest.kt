package de.tillhub.inputengine.formatter

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.formatter.MoneyFormatterTest.Companion.LOCALE
import kotlin.test.Test
import kotlin.test.assertEquals

class QuantityFormatterTest {

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

    @Test
    fun format_returnsZeroForSubEpsilonValue() {
        // Tiny value below 1E-6 should be shown as zero
        val tiny = QuantityIO.of(0.00000001)
        val formatted = QuantityFormatter.format(tiny, minFractionDigits = 2, locale = "en_US")
        assertEquals("0.00", formatted)
    }

    @Test
    fun format_simpleIntegerValue() {
        val qty = QuantityIO.of(42)
        val formatted = QuantityFormatter.format(qty, minFractionDigits = 2, locale = "en_US")
        // 42.00 with grouping (could be "42.00" or "42.00" with grouping)
        assertEquals("42.00", formatted.replace(",", ""))
    }

    @Test
    fun format_largeValue_withGrouping() {
        val qty = QuantityIO.of(1234567.89)
        val formatted = QuantityFormatter.format(qty, minFractionDigits = 2, locale = "en_US")
        // Should group: 1,234,567.89
        assertEquals("1,234,567.89", formatted)
    }

    @Test
    fun format_negativeValue() {
        val qty = QuantityIO.of(-12.3456)
        val formatted = QuantityFormatter.format(qty, minFractionDigits = 4, locale = "en_US")
        assertEquals("-12.3456", formatted.replace(",", ""))
    }

    @Test
    fun format_value_in_different_locale() {
        val qty = QuantityIO.of(1234.5)
        val formatted = QuantityFormatter.format(qty, minFractionDigits = 2, locale = "de_DE")
        // German locale should use comma for decimal and dot for grouping: "1.234,50"
        assertEquals("1.234,50", formatted)
    }
}
