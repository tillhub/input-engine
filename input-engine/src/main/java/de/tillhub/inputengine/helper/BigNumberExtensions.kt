package de.tillhub.inputengine.helper

import de.tillhub.inputengine.data.Digit
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

// BigDecimal

fun pow10decimal(exp: Int): BigDecimal = BigDecimal.TEN.pow(exp)

fun BigDecimal.isPositive(includeZero: Boolean = false): Boolean {
    return when (includeZero) {
        true -> signum().let { it == 0 || it == 1 }
        false -> signum() == 1
    }
}

fun BigDecimal.isNegative(includeZero: Boolean = false): Boolean {
    return when (includeZero) {
        true -> signum().let { it == 0 || it == -1 }
        false -> signum() == -1
    }
}

fun BigDecimal.isZero(): Boolean = signum() == 0

fun BigDecimal.isDecimalFraction(): Boolean = this < BigDecimal.ONE && this > BigDecimal.ZERO

fun BigDecimal.roundToBigInteger(roundingMode: RoundingMode): BigInteger =
    this.setScale(0, roundingMode).toBigInteger()

// BigInteger

fun pow10(exp: Int): BigInteger = BigInteger.TEN.pow(exp)

fun BigInteger.isPositive(includeZero: Boolean = false): Boolean {
    return when (includeZero) {
        true -> signum().let { it == 0 || it == 1 }
        false -> signum() == 1
    }
}

fun BigInteger.isNegative(includeZero: Boolean = false): Boolean {
    return when (includeZero) {
        true -> signum().let { it == 0 || it == -1 }
        false -> signum() == -1
    }
}

fun BigInteger.isZero(): Boolean = signum() == 0

object BigIntegers {

    /**
     * Builds a [BigInteger] which represent a floating point value defined by the given [majorDigits] and
     * [fractionDigits]. [fractions] the defines the digit count which will be used to represent the [fractionDigits].
     * This means that the [fractionDigits] might get cut of at the end if they exceed the space defined by
     * [fractions]. If there are less [fractionDigits] then [fractions] 0 will be added at the end.
     *
     * i.e. given 1,2,3 as [majorDigits] and 4,5,6,7 as [fractionDigits]:
     * - [fractions]=2 will result in BigInteger(12345)
     * - [fractions]=4 will result in BigInteger(1234567)
     * - [fractions]=6 will result in BigInteger(123456700)
     */
    fun of(majorDigits: List<Digit>, fractionDigits: List<Digit>, fractions: Int = 6): BigInteger {
        val majorValue = majorDigits.fold(BigInteger.ZERO) { acc, item ->
            acc.multiply(BigInteger.TEN).add(item.value.toBigInteger())
        }

        var fullFractionDigits = fractionDigits.take(fractions)
        fullFractionDigits = fullFractionDigits.toMutableList().apply {
            val missingDigits = fractions - fullFractionDigits.size
            repeat(missingDigits) { add(Digit.ZERO) }
        }

        val quantityValue = fullFractionDigits.fold(majorValue) { acc, item ->
            acc.multiply(BigInteger.TEN).add(item.value.toBigInteger())
        }

        return quantityValue
    }
}
