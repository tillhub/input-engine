package de.tillhub.inputengine.domain.helper

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.data.QuantityIO
import de.tillhub.inputengine.domain.Digit
import kotlin.test.Test
import kotlin.test.assertEquals

class FinancialExtensionsTest {

    @Test
    fun testGetMajorDigits() {
        val quantity = QuantityIO.of(BigDecimal.parseString("12.34"))

        val expected = listOf(Digit.ONE, Digit.TWO)
        val actual = quantity.getMajorDigits()

        assertEquals(expected, actual)
    }

    @Test
    fun testGetMinorDigits() {
        val quantity = QuantityIO.of(BigDecimal.parseString("12.34"))

        val expected = listOf(Digit.THREE, Digit.FOUR)
        val actual = quantity.getMinorDigits()

        assertEquals(expected, actual)
    }

    @Test
    fun testGetMinorDigits_WithLeadingZeros() {
        val quantity = QuantityIO.of(BigDecimal.parseString("12.04"))

        val expected = listOf(Digit.ZERO, Digit.FOUR)
        val actual = quantity.getMinorDigits()

        assertEquals(expected, actual)
    }

    @Test
    fun testGetMajorAndMinorDigits_ZeroValue() {
        val quantity = QuantityIO.of(BigInteger.ZERO)

        assertEquals(listOf(Digit.ZERO), quantity.getMajorDigits())
        assertEquals(emptyList(), quantity.getMinorDigits()) // minorDigits skips 0 entirely
    }
}
