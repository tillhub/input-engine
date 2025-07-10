package de.tillhub.inputengine.domain.helper

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BigNumberExtensionsTest {

    @Test
    fun testPow10_BigInteger() {
        assertEquals(BigInteger.ONE, pow10(0))
        assertEquals(BigInteger.TEN, pow10(1))
        assertEquals(BigInteger.parseString("100"), pow10(2))
        assertEquals(BigInteger.parseString("1000000"), pow10(6))
    }

    @Test
    fun testPow10_BigDecimal() {
        assertEquals(BigDecimal.ONE, pow10decimal(0))
        assertEquals(BigDecimal.TEN, pow10decimal(1))
        assertEquals(BigDecimal.parseString("100"), pow10decimal(2))
        assertEquals(BigDecimal.parseString("100000000"), pow10decimal(8))
    }

    @Test
    fun testIsPositive_BigInteger_Default() {
        assertTrue(BigInteger.ONE.isPositive())
        assertFalse(BigInteger.ZERO.isPositive())
        assertFalse(BigInteger.parseString("-1").isPositive())
    }

    @Test
    fun testIsPositive_BigInteger_IncludeZero() {
        assertTrue(BigInteger.ONE.isPositive(includeZero = true))
        assertTrue(BigInteger.ZERO.isPositive(includeZero = true))
        assertFalse(BigInteger.parseString("-10").isPositive(includeZero = true))
    }
}
