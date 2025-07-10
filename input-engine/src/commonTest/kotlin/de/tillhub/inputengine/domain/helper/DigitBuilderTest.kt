package de.tillhub.inputengine.domain.helper

import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.domain.Digit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DigitBuilderTest {

    @Test
    fun testDigits_PositiveNumbers() {
        assertEquals(
            listOf(Digit.ZERO),
            DigitBuilder.digits(BigInteger.ZERO),
        )

        assertEquals(
            listOf(Digit.ONE),
            DigitBuilder.digits(BigInteger.ONE),
        )

        assertEquals(
            listOf(Digit.ONE, Digit.TWO, Digit.THREE, Digit.FOUR),
            DigitBuilder.digits(BigInteger.parseString("1234")),
        )

        assertEquals(
            listOf(Digit.ONE, Digit.TWO, Digit.ZERO, Digit.ZERO),
            DigitBuilder.digits(BigInteger.parseString("1200")),
        )
    }

    @Test
    fun testDigits_NegativeNumbers() {
        assertEquals(
            listOf(Digit.ONE, Digit.TWO, Digit.THREE),
            DigitBuilder.digits(BigInteger.parseString("-123")),
        )

        assertEquals(
            listOf(Digit.ZERO),
            DigitBuilder.digits(BigInteger.parseString("-0")),
        )
    }

    @Test
    fun testMinorDigits_ExactLength() {
        val result = DigitBuilder.minorDigits(BigInteger.parseString("1234"), fractionCount = 4)
        assertEquals(listOf(Digit.ONE, Digit.TWO, Digit.THREE, Digit.FOUR), result)
    }

    @Test
    fun testMinorDigits_WithLeadingZeros() {
        val result = DigitBuilder.minorDigits(BigInteger.parseString("1234"), fractionCount = 6)
        assertEquals(listOf(Digit.ZERO, Digit.ZERO, Digit.ONE, Digit.TWO, Digit.THREE, Digit.FOUR), result)
    }

    @Test
    fun testMinorDigits_Zero() {
        val result = DigitBuilder.minorDigits(BigInteger.ZERO, fractionCount = 3)
        assertEquals(emptyList(), result)
    }

    @Test
    fun testMinorDigits_InvalidFractionCount() {
        assertFailsWith<IllegalArgumentException> {
            DigitBuilder.minorDigits(BigInteger.ONE, fractionCount = 0)
        }
    }
}
