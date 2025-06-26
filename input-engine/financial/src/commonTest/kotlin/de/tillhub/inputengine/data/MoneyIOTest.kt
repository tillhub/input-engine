package de.tillhub.inputengine.data

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.financial.data.MoneyIO
import de.tillhub.inputengine.financial.helper.EUR
import de.tillhub.inputengine.financial.helper.eur
import de.tillhub.inputengine.financial.helper.usd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoneyIOTest {

    @Test
    fun testIntConstructors() {
        assertEquals(1.56.toBigDecimal(), MoneyIO.of(156, EUR).amount)
        assertEquals(78.94.toBigDecimal(), MoneyIO.of(7894, EUR).amount)
        assertEquals(100, MoneyIO.of(100, EUR).toInt())
        assertEquals(100L, MoneyIO.of(100, EUR).toLong())
        assertEquals(100.0, MoneyIO.of(100, EUR).toDouble())
        assertEquals(100.0f, MoneyIO.of(100, EUR).toFloat())
        assertEquals(1.0.eur, MoneyIO.of(100, EUR))
    }

    @Test
    fun testDoubleConstructors() {
        assertEquals(1.7856.toBigDecimal(), MoneyIO.of(178.56, EUR).amount)
        assertEquals(178.56, MoneyIO.of(178.56, EUR).toDouble())
        assertEquals(178, MoneyIO.of(178.56, EUR).toInt())
        assertEquals(1.7856.eur, MoneyIO.of(178.56, EUR))
    }

    @Test
    fun testBigDecimalConstructors() {
        val decimal = 178.56.toBigDecimal()
        assertEquals(1.7856.toBigDecimal(), MoneyIO.of(decimal, EUR).amount)
        assertEquals(178.56, MoneyIO.of(decimal, EUR).toDouble())
        assertEquals(178, MoneyIO.of(decimal, EUR).toInt())
    }

    @Test
    fun testBigIntegerConstructors() {
        assertEquals(1.56.toBigDecimal(), MoneyIO.of(BigInteger.fromInt(156).intValue(), EUR).amount)
        assertEquals(7894, MoneyIO.of(BigInteger.fromInt(7894).intValue(), EUR).toInt())
    }

    @Test
    fun testIsZero() {
        assertTrue(MoneyIO.zero(EUR).isZero())
        assertFalse(MoneyIO.zero(EUR).isNotZero())
        assertTrue(MoneyIO.of(BigInteger.ZERO.intValue(), EUR).isZero())
        assertFalse(MoneyIO.of(BigInteger.ZERO.intValue(), EUR).isNotZero())
    }

    @Test
    fun testIsNegative() {
        assertTrue((-10).eur.isNegative())
        assertFalse(10.eur.isNegative())
        assertTrue((-10.0).eur.isNegative())
        assertFalse(10.0.eur.isNegative())
    }

    @Test
    fun testIsPositive() {
        assertFalse((-10).eur.isPositive())
        assertTrue(10.eur.isPositive())
        assertTrue(0.eur.isPositive(true))
        assertFalse((-10.0).eur.isPositive())
        assertTrue(10.0.eur.isPositive())
        assertFalse(0.eur.isPositive())
    }

    @Test
    fun testIsValid() {
        assertTrue((-10).eur.isValid())
        assertTrue(10.eur.isValid())
        assertFalse(1000000001.eur.isValid())
        assertFalse((-1000000001.0).eur.isValid())
        assertTrue(10.0.eur.isValid())
        assertFalse(10000001.eur.isValid())
    }

    @Test
    fun testNegate() {
        assertEquals((-10).eur, 10.eur.negate())
        assertEquals(6.eur, (-6).eur.negate())
    }

    @Test
    fun testAbs() {
        assertEquals(6.eur, (-6).eur.abs())
        assertEquals(6.eur, 6.eur.abs())
    }

    @Test
    fun testAppend() {
        assertEquals(10.01.eur, MoneyIO.append(1.eur, Digit.ONE.value))
        assertEquals((-0.11).toBigDecimal().eur, MoneyIO.append((-0.01).eur, Digit.ONE.value))
        assertEquals(10.01.toBigDecimal().eur, MoneyIO.append(1.toBigDecimal().eur, Digit.ONE.value))
        assertEquals(1000000001.toBigInteger().eur, MoneyIO.append(1000000001.toBigInteger().eur, Digit.ONE.value))
    }

    @Test
    fun testCompareTo_Throws() {
        assertFailsWith<IllegalArgumentException> {
            10.toBigInteger().eur.compareTo(10.toBigInteger().usd)
        }
        assertFailsWith<IllegalArgumentException> {
            10.0.toBigDecimal().eur.compareTo(10.toBigDecimal().usd)
        }
    }

    @Test
    fun testCompareTo_LessThan() {
        assertTrue((-10).toBigInteger().eur < (-9).toBigInteger().eur)
        assertTrue(9.toBigInteger().eur < 10.toBigInteger().eur)
        assertTrue((-10.00).toBigDecimal().eur < (-9.00).toBigDecimal().eur)
        assertTrue(10.00.toBigDecimal().eur < 10.01.toBigDecimal().eur)
        assertTrue(1.23456788.toBigDecimal().eur < 1.23456789.toBigDecimal().eur)
    }

    @Test
    fun testCompareTo_Equal() {
        assertTrue((-10).toBigInteger().eur == (-10).toBigInteger().eur)
        assertTrue(10.toBigInteger().eur == 10.toBigInteger().eur)
        assertTrue((-10.00).toBigDecimal().eur == (-10.00).toBigDecimal().eur)
        assertTrue(10.00.toBigDecimal().eur == 10.00.toBigDecimal().eur)
        assertTrue(1.23456789.toBigDecimal().eur == 1.23456789.toBigDecimal().eur)
    }

    @Test
    fun testCompareTo_GreaterThan() {
        assertTrue((-9.00).toBigDecimal().eur > (-10.00).toBigDecimal().eur)
        assertTrue(10.01.toBigDecimal().eur > 10.00.toBigDecimal().eur)
        assertTrue(1.23456789.toBigDecimal().eur > 1.23456788.toBigDecimal().eur)
    }

    @Test
    fun testRemoveLastDigit() {
        assertEquals(0.eur, MoneyIO.removeLastDigit(0.01.eur))
        assertEquals(0.01.eur, MoneyIO.removeLastDigit(0.11.eur))
        assertEquals(1.10.eur, MoneyIO.removeLastDigit(11.0.eur))
    }

    @Test
    fun testUnaryMinus() {
        assertEquals((-10).eur, (-10).eur)
        assertEquals(10.eur, -(-10).eur)
    }

    @Test
    fun testByteAndShortConversion() {
        assertEquals(1.toByte(), 1.eur.toByte())
        assertEquals(100.toShort(), 1.eur.toShort())
    }

    @Test
    fun testEqualsAndHashCode() {
        val m1 = 10.eur
        val m2 = MoneyIO.of(1000, EUR)
        assertTrue(m1 == m2)
        assertEquals(m1.hashCode(), m2.hashCode())
    }
}

