package de.tillhub.inputengine.domain.helper

import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.domain.Digit

object DigitBuilder {

    /**
     * Returns the digits of the given [value]. In case the given value is 0 the resulting list will contain the
     * zero digit as well.
     * i.e. 0 will result in [0]; 1234 will result in [1,2,3,4]; 1200 will result in [1,2,0,0]
     */
    fun digits(value: BigInteger): List<Digit> =
        mutableListOf<Digit>().also {
            var currentValue = value.abs()
            while (currentValue.isPositive(includeZero = false)) {
                val digitValue = currentValue.mod(BigInteger.Companion.TEN).intValue()
                it.add(Digit.Companion.from(digitValue))
                currentValue = currentValue.divide(BigInteger.Companion.TEN)
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
    fun minorDigits(minorValue: BigInteger, fractionCount: Int): List<Digit> {
        require(fractionCount >= 1) { "fractionCount must be at least 1" }

        return mutableListOf<Digit>().also { list ->
            var currentValue = minorValue
            var zerosAtEnd = 0
            while (currentValue.isPositive(includeZero = false)) {
                val digitValue = currentValue.mod(BigInteger.Companion.TEN).intValue()
                if (list.isNotEmpty() || digitValue != 0) {
                    list.add(Digit.Companion.from(digitValue))
                } else {
                    zerosAtEnd += 1
                }
                currentValue = currentValue.divide(BigInteger.Companion.TEN)
            }

            if (minorValue.isPositive(includeZero = false)) {
                val leadingZeros = fractionCount - zerosAtEnd - list.size
                repeat(leadingZeros) { list.add(Digit.ZERO) }
            }
            list.reverse()
        }
    }
}
