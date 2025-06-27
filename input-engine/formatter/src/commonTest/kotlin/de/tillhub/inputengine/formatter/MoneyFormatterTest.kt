package de.tillhub.inputengine.formatter

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.financial.data.MoneyIO
import de.tillhub.inputengine.financial.helper.EUR
import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatterTest {

    @Test
    fun testFormatInt() {
        val result = MoneyFormatter.format(
            MoneyIO.Companion.of(156, EUR),
            LOCALE,
        )
        assertEquals("1,56 €", result)
    }

    @Test
    fun testFormatDouble() {
        val result = MoneyFormatter.format(
            MoneyIO.Companion.of(100.0, EUR),
            LOCALE,
        )
        assertEquals("1,00 €", result)
    }

    @Test
    fun testFormatBigInteger() {
        val result = MoneyFormatter.format(
            MoneyIO.of(BigInteger.fromInt(100), EUR),
            LOCALE,
        )
        assertEquals("1,00 €", result)
    }

    @Test
    fun testZeroWithBigDecimal() {
        val result = MoneyFormatter.format(
            MoneyIO.of(BigDecimal.ZERO, EUR),
            LOCALE,
        )
        assertEquals("0,00 €", result)
    }

    @Test
    fun testZeroWithBigInteger() {
        val result = MoneyFormatter.format(
            MoneyIO.of(BigInteger.ZERO, EUR),
            LOCALE,
        )
        assertEquals("0,00 €", result)
    }

    companion object {
        const val LOCALE = "de"
    }
}
