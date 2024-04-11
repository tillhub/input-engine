package de.tillhub.inputengine.helper

import androidx.annotation.IntRange
import de.tillhub.inputengine.data.Digit
import java.math.BigDecimal
import java.math.BigInteger

// BigDecimal
fun pow10decimal(exp: Int): BigDecimal = BigDecimal.TEN.pow(exp)

// BigInteger
fun pow10(exp: Int): BigInteger = BigInteger.TEN.pow(exp)

fun BigInteger.isPositive(includeZero: Boolean = false): Boolean {
    return when (includeZero) {
        true -> signum().let { it == 0 || it == 1 }
        false -> signum() == 1
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

    /**
     * Returns the digits of the given [value]. In case the given value is 0 the resulting list will contain the
     * zero digit as well.
     * i.e. 0 will result in [0]; 1234 will result in [1,2,3,4]; 1200 will result in [1,2,0,0]
     */
    fun digits(value: BigInteger): List<Digit> =
        mutableListOf<Digit>().also {
            var currentValue = value
            while (currentValue.isPositive(includeZero = false)) {
                val digitValue = currentValue.mod(BigInteger.TEN).toInt()
                it.add(Digit.from(digitValue))
                currentValue = currentValue.divide(BigInteger.TEN)
            }

            if (it.isEmpty()) it.add(Digit.ZERO)

            it.reverse()
        }

    /**
     * Converts the given [minorValue] (the decimal part of a floating point value: i.e. 1.2345 -> minorValue=2345)
     * to a list of digits. i.e. 1234 will result in [1,2,3,4].
     * The [fractionCount] defines the amount of decimal places the [minorValue] may consist of. The [minorValue]
     * must NOT have more digits then defined in [fractionCount]. In case the [minorValue] has less digits
     * then defined by [fractionCount] the resulting list will contain leading 0 digits.
     * i.e. a value of 0.001234 ([minorValue] of 1234 and [fractionCount] of 6) will result in [0,0,1,2,3,4]
     */
    fun minorDigits(minorValue: BigInteger, @IntRange(from = 1) fractionCount: Int): List<Digit> =
        mutableListOf<Digit>().also { list ->
            var currentValue = minorValue
            var zerosAtEnd = 0
            while (currentValue.isPositive(includeZero = false)) {
                val digitValue = currentValue.mod(BigInteger.TEN).toInt()
                if (list.isNotEmpty() || digitValue != 0) {
                    list.add(Digit.from(digitValue))
                } else {
                    zerosAtEnd += 1
                }
                currentValue = currentValue.divide(BigInteger.TEN)
            }

            if (minorValue.isPositive(includeZero = false)) {
                val leadingZeros = fractionCount - zerosAtEnd - list.size
                repeat(leadingZeros) { list.add(Digit.ZERO) }
            }
            list.reverse()
        }
}
