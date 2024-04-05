package de.tillhub.inputengine.data

import android.os.Parcelable
import de.tillhub.inputengine.helper.BigIntegers
import de.tillhub.inputengine.helper.isNegative
import de.tillhub.inputengine.helper.isPositive
import de.tillhub.inputengine.helper.isZero
import de.tillhub.inputengine.helper.pow10
import de.tillhub.inputengine.helper.pow10decimal
import de.tillhub.inputengine.helper.roundToBigInteger
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

@Suppress("TooManyFunctions")
@Parcelize
data class Quantity internal constructor(
    val value: BigInteger
) : Parcelable, Comparable<Quantity> {

    @IgnoredOnParcel
    val decimal: BigDecimal by lazy {
        // TODO check rounding
        value.toBigDecimal().divide(FRACTIONS_FACTOR, PRECISION)
    }

    fun add(other: Quantity): Quantity = Quantity(value.add(other.value))
    fun subtract(other: Quantity): Quantity = Quantity(value.subtract(other.value))
    fun round(roundingMode: RoundingMode = RoundingMode.HALF_UP): Quantity = Quantity(
        decimal.roundToBigInteger(roundingMode).multiply(FRACTIONS_FACTOR_INT)
    )

    operator fun times(other: Quantity): Quantity =
        Quantity(value.multiply(other.value).divide(FRACTIONS_FACTOR_INT))

    operator fun div(other: Quantity): Quantity = of(
        value.toBigDecimal().divide(other.value.toBigDecimal(), FRACTIONS, RoundingMode.HALF_UP)
    )

    fun isNegative(includeZero: Boolean = false): Boolean = value.isNegative(includeZero)
    fun isPositive(includeZero: Boolean = false): Boolean = value.isPositive(includeZero)
    fun isZero(): Boolean = value.isZero()
    fun isMoreThenOne() = this > ONE

    @IgnoredOnParcel
    val majorValue: BigInteger = value.divide(FRACTIONS_FACTOR_INT)

    @IgnoredOnParcel
    val majorDigits: List<Digit> by lazy { Digits.digits(majorValue) }

    @IgnoredOnParcel
    val minorDigits: List<Digit> by lazy {
        val minorValue = value.mod(FRACTIONS_FACTOR_INT)
        Digits.minorDigits(minorValue, FRACTIONS)
    }

    /**
     * Returns the next smaller quantity amount. In case the current quantity has fraction values the result is
     * rounded down to the next smaller integer value. The result will never be smaller then 1 in case [allowsZero]
     * is false. Otherwise the result will never be smaller then 0.
     *
     * i.e.
     * - 4.5 returns 4.0
     * - 4.0 returns 3.0
     * - 1.001 returns 1.0
     * - 1.0 returns 1.0
     */
    @Suppress("ComplexMethod", "NestedBlockDepth")
    fun nextSmaller(allowsZero: Boolean = false, allowsNegatives: Boolean = false): Quantity =
        when (hasFractions) {
            true -> when (isPositive(includeZero = false) && majorValue != BigInteger.ZERO) {
                true -> majorValue.multiply(FRACTIONS_FACTOR_INT)
                false -> when (allowsNegatives && !allowsZero) {
                    true -> majorValue.minus(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                    false -> majorValue.multiply(FRACTIONS_FACTOR_INT)
                }
            }

            false -> when (allowsNegatives) {
                true -> when (allowsZero) {
                    true -> majorValue.minus(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                    false -> {
                        val tempValue =
                            majorValue.minus(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                        if (tempValue == BigInteger.ZERO) {
                            tempValue.minus(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                        } else {
                            tempValue
                        }
                    }
                }

                false -> {
                    val lowerBound = if (allowsZero) BigInteger.ZERO else BigInteger.ONE
                    when (majorValue == lowerBound) {
                        true -> value
                        false -> majorValue.minus(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                    }
                }
            }
        }.let { Quantity(it) }

    /**
     * Returns the next larger quantity amount. In case the current quantity has fraction values the result is
     * rounded up to the next larger integer value.
     *
     * i.e.
     * - 4.5 returns 5.0
     * - 4.0 returns 5.0
     * - 1.001 returns 2.0
     */
    @Suppress("NestedBlockDepth")
    fun nextLarger(allowsZero: Boolean = false, maxQuantity: Quantity = MAX_VALUE): Quantity =
        when (hasFractions) {
            true -> when (allowsZero) {
                true -> {
                    val nextValue =
                        Quantity(majorValue.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT))
                    if (nextValue <= maxQuantity) {
                        nextValue
                    } else {
                        Quantity(majorValue.multiply(FRACTIONS_FACTOR_INT))
                    }
                }

                false -> {
                    val nextValue =
                        Quantity(majorValue.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT))
                    val tempValue =
                        if (isPositive()) {
                            if (nextValue <= maxQuantity) {
                                nextValue
                            } else {
                                this
                            }
                        } else {
                            Quantity(majorValue.multiply(FRACTIONS_FACTOR_INT))
                        }
                    if (tempValue.value == BigInteger.ZERO) {
                        Quantity(tempValue.value.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT))
                    } else {
                        tempValue
                    }
                }
            }

            false -> when (allowsZero) {
                true -> {
                    val nextValue =
                        Quantity(majorValue.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT))
                    if (nextValue <= maxQuantity) {
                        nextValue
                    } else {
                        Quantity(majorValue.multiply(FRACTIONS_FACTOR_INT))
                    }
                }

                false -> {
                    val nextValue =
                        Quantity(majorValue.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT))
                    val tempValue = if (nextValue <= maxQuantity) {
                        nextValue
                    } else {
                        Quantity(majorValue.multiply(FRACTIONS_FACTOR_INT))
                    }
                    if (tempValue.value == BigInteger.ZERO) {
                        Quantity(
                            tempValue.value.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                        )
                    } else {
                        tempValue
                    }
                }
            }
        }

    @IgnoredOnParcel
    val hasFractions = value.mod(FRACTIONS_FACTOR_INT).isPositive(includeZero = false)

    /**
     * Returns if the current Quantity is higher than the max value
     */
    fun isValid(): Boolean = this <= MAX_VALUE

    operator fun unaryMinus() = Quantity(-value)

    operator fun rem(other: Quantity): Quantity = subtract(other)

    operator fun plus(other: Quantity): Quantity = add(other)
    operator fun minus(other: Quantity): Quantity = subtract(other)

    override fun compareTo(other: Quantity): Int = value.compareTo(other.value)

    companion object {

        private val PRECISION: MathContext = MathContext.DECIMAL128
        const val FRACTIONS = 6

        private val FRACTIONS_FACTOR: BigDecimal = pow10decimal(FRACTIONS)
        private val FRACTIONS_FACTOR_INT: BigInteger = pow10(FRACTIONS)

        // MAX_VALUE for the Quantity class is 10 000
        val MAX_VALUE: Quantity = Quantity(BigInteger.valueOf(10000).multiply(FRACTIONS_FACTOR_INT))
        val ZERO: Quantity = Quantity(BigInteger.ZERO)
        val ONE: Quantity = Quantity(BigInteger.ONE.multiply(FRACTIONS_FACTOR_INT))

        fun of(value: BigDecimal): Quantity =
            Quantity(value.multiply(FRACTIONS_FACTOR, PRECISION).toBigInteger())

        fun of(majorDigits: List<Digit>, fractionDigits: List<Digit>): Quantity =
            Quantity(BigIntegers.of(majorDigits, fractionDigits, FRACTIONS))

        fun sumAll(quantities: List<Quantity>): Quantity = Quantity(quantities.sumOf { it.value })
    }
}