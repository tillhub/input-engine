package de.tillhub.inputengine.formatter

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.financial.data.QuantityIO
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

class QuantityFormatterAndroidTest {

    @Test
    fun testFormatInt() {
        assertEquals("1", QuantityFormatter.format(QuantityIO.of(1), locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("10,00", QuantityFormatter.format(QuantityIO.of(10), 2, locale = Locale.GERMAN.toLanguageTag()))
    }

    @Test
    fun testFormatDouble() {
        assertEquals("1,23", QuantityFormatter.format(QuantityIO.of(1.23), locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("1", QuantityFormatter.format(QuantityIO.of(1.00), locale = Locale.GERMAN.toLanguageTag()))
    }

    @Test
    fun testFormatBigDecimal() {
        assertEquals("1,23", QuantityFormatter.format(QuantityIO.of(BigDecimal.fromDouble(1.23)), locale = Locale.GERMAN.toLanguageTag()))
        assertEquals("1", QuantityFormatter.format(QuantityIO.of(BigDecimal.fromDouble(1.00)), locale = Locale.GERMAN.toLanguageTag()))
    }
}
