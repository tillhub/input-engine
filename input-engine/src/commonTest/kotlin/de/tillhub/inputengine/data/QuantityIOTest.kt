package de.tillhub.inputengine.data

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.domain.Digit
import de.tillhub.inputengine.domain.helper.getMajorDigits
import de.tillhub.inputengine.domain.helper.getMinorDigits
import de.tillhub.inputengine.domain.helper.toBigDecimal
import de.tillhub.inputengine.domain.helper.toBigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuantityIOTest {
    @Test
    fun testConstructors_Int() {
        assertEquals(10000.toBigInteger(), QuantityIO.of(1).value)
        assertEquals(1.toBigDecimal(), QuantityIO.of(1).getDecimal())
        assertEquals(250000.toBigInteger(), QuantityIO.of(25).value)
        assertEquals(100000000.toBigInteger(), QuantityIO.of(10000).value)
        assertEquals(1, QuantityIO.of(1).toInt())
        assertEquals(1.0, QuantityIO.of(1).toDouble())
    }

    @Test
    fun testConstructors_Double() {
        assertEquals(10000.toBigInteger(), QuantityIO.of(1.00).value)
        assertEquals(257800.toBigInteger(), QuantityIO.of(25.78).value)
        assertEquals(3289800.toBigInteger(), QuantityIO.of(328.98).value)
        assertEquals(328.98, QuantityIO.of(328.98).toDouble())
        assertEquals(328, QuantityIO.of(328.98).toInt())
    }

    @Test
    fun testConstructors_BigInteger() {
        assertEquals(10000.toBigInteger(), QuantityIO.of(BigInteger.ONE).value)
        assertEquals(1, QuantityIO.of(BigInteger.ONE).toInt())
        assertEquals(100000.toBigInteger(), QuantityIO.of(BigInteger.TEN).value)
        assertEquals(10.0, QuantityIO.of(BigInteger.TEN).toDouble())
    }

    @Test
    fun testConstructors_BigDecimal() {
        assertEquals(10000.toBigInteger(), QuantityIO.of(BigDecimal.ONE).value)
        assertEquals(1, QuantityIO.of(BigDecimal.ONE).toInt())
        assertEquals(100000.toBigInteger(), QuantityIO.of(BigDecimal.TEN).value)
        assertEquals(10.0, QuantityIO.of(BigDecimal.TEN).toDouble())
    }

    @Test
    fun testMajorDigits() {
        assertEquals(listOf(Digit.ONE, Digit.TWO, Digit.THREE), QuantityIO.of(123).getMajorDigits())
        assertEquals(listOf(Digit.NINE, Digit.EIGHT), QuantityIO.of(98).getMajorDigits())
    }

    @Test
    fun testMinorDigits() {
        assertEquals(emptyList(), QuantityIO.of(98).getMinorDigits())
        assertEquals(listOf(Digit.FOUR, Digit.FIVE), QuantityIO.of(98.45).getMinorDigits())
        assertEquals(listOf(Digit.THREE, Digit.SIX, Digit.NINE), QuantityIO.of(0.369).getMinorDigits())
        assertEquals(listOf(Digit.THREE, Digit.SIX, Digit.NINE, Digit.TWO), QuantityIO.of(0.3692).getMinorDigits())
        assertEquals(listOf(Digit.THREE, Digit.SIX, Digit.NINE, Digit.TWO), QuantityIO.of(0.36921).getMinorDigits())
    }

    @Test
    fun testHasFractions() {
        assertFalse(QuantityIO.of(45).hasFractions())
        assertTrue(QuantityIO.of(45.56).hasFractions())
        assertTrue(QuantityIO.of(0.1).hasFractions())
    }

    @Test
    fun testIsPositive() {
        assertFalse(QuantityIO.ZERO.isPositive(false))
        assertTrue(QuantityIO.ZERO.isPositive(true))
        assertTrue(QuantityIO.MAX_VALUE.isPositive())
        assertFalse(QuantityIO.MIN_VALUE.isPositive())
    }

    @Test
    fun testIsNegative() {
        assertFalse(QuantityIO.ZERO.isNegative())
        assertFalse(QuantityIO.MAX_VALUE.isNegative())
        assertTrue(QuantityIO.MIN_VALUE.isNegative())
    }

    @Test
    fun testIsZero() {
        assertTrue(QuantityIO.ZERO.isZero())
        assertFalse(QuantityIO.of(1).isZero())
    }

    @Test
    fun testNextSmaller() {
        assertEquals(QuantityIO.of(4), QuantityIO.of(4.5).nextSmaller())
        assertEquals(QuantityIO.of(3), QuantityIO.of(4).nextSmaller())
        assertEquals(QuantityIO.of(1), QuantityIO.of(1.001).nextSmaller())
        assertEquals(QuantityIO.of(1), QuantityIO.of(1).nextSmaller())
        assertEquals(QuantityIO.ZERO, QuantityIO.of(1).nextSmaller(true))
        assertEquals(QuantityIO.of(-1), QuantityIO.ZERO.nextSmaller(allowsNegatives = true))
    }

    @Test
    fun testNextLarger() {
        assertEquals(QuantityIO.of(5), QuantityIO.of(4.5).nextLarger())
        assertEquals(QuantityIO.of(5), QuantityIO.of(4).nextLarger())
        assertEquals(QuantityIO.of(2), QuantityIO.of(1.01).nextLarger())
        assertEquals(QuantityIO.of(1), QuantityIO.of(-1).nextLarger())
        assertEquals(QuantityIO.ZERO, QuantityIO.of(-1).nextLarger(true))
        assertEquals(QuantityIO.MAX_VALUE, QuantityIO.MAX_VALUE.nextLarger())
    }
}
