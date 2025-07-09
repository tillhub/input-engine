package de.tillhub.inputengine.data

import de.tillhub.inputengine.domain.helper.toBigDecimal
import de.tillhub.inputengine.domain.helper.toBigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PercentIOTest {

    @Test
    fun testConstructors_Int() {
        assertEquals(PercentIO.ZERO, PercentIO.of(0))
        assertEquals(100L, PercentIO.of(1).value)
        assertEquals(5600L, PercentIO.of(56).value)
        assertEquals(10000L, PercentIO.of(100).value)
        assertEquals(PercentIO.WHOLE, PercentIO.of(100))
        assertEquals(56, PercentIO.of(56).toInt())
        assertEquals(56.0, PercentIO.of(56).toDouble())
        assertEquals(0.56, PercentIO.of(56).toRatio())
    }

    @Test
    fun testConstructors_Long() {
        assertEquals(100L, PercentIO.of(1L).value)
        assertEquals(5600L, PercentIO.of(56L).value)
        assertEquals(10000L, PercentIO.of(100L).value)
        assertEquals(56L, PercentIO.of(56L).toLong())
    }

    @Test
    fun testConstructors_Double() {
        assertEquals(100L, PercentIO.of(1.0).value)
        assertEquals(560L, PercentIO.of(5.6).value)
        assertEquals(5607L, PercentIO.of(56.07).value)
        assertEquals(10000L, PercentIO.of(100.0).value)
        assertEquals(56.07, PercentIO.of(56.07).toDouble())
        assertEquals(0.5607, PercentIO.of(56.07).toRatio())
    }

    @Test
    fun testConstructors_BigInteger() {
        assertEquals(100L, PercentIO.of(1.toBigInteger()).value)
        assertEquals(5600L, PercentIO.of(56.toBigInteger()).value)
        assertEquals(10000L, PercentIO.of(100.toBigInteger()).value)
    }

    @Test
    fun testConstructors_BigDecimal() {
        assertEquals(100L, PercentIO.of(1.0.toBigDecimal()).value)
        assertEquals(560L, PercentIO.of(5.6.toBigDecimal()).value)
        assertEquals(5607L, PercentIO.of(56.07.toBigDecimal()).value)
        assertEquals(10000L, PercentIO.of(100.0.toBigDecimal()).value)
    }

    @Test
    fun testIsNotZero() {
        assertFalse(PercentIO.ZERO.isNotZero())
        assertTrue(PercentIO.WHOLE.isNotZero())
    }
}