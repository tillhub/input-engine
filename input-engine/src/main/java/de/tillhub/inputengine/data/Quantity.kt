package de.tillhub.inputengine.data

import android.os.Parcelable
import de.tillhub.inputengine.helper.BigIntegers
import de.tillhub.inputengine.helper.isPositive
import de.tillhub.inputengine.helper.isZero
import de.tillhub.inputengine.helper.pow10
import de.tillhub.inputengine.helper.pow10decimal
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
        value.toBigDecimal().divide(FRACTIONS_FACTOR, PRECISION)
    }

    operator fun unaryMinus() = Quantity(-value)

    operator fun plus(other: Quantity): Quantity = Quantity(value.add(other.value))
    operator fun minus(other: Quantity): Quantity = Quantity(value.subtract(other.value))

    operator fun times(other: Quantity): Quantity =
        Quantity(value.multiply(other.value).divide(FRACTIONS_FACTOR_INT))

    operator fun div(other: Quantity): Quantity = of(
        value.toBigDecimal().divide(other.value.toBigDecimal(), FRACTIONS, RoundingMode.HALF_UP)
    )

    private fun isPositive(includeZero: Boolean = false): Boolean = value.isPositive(includeZero)
    fun isZero(): Boolean = value.isZero()

    @IgnoredOnParcel
    val majorValue: BigInteger = value.divide(FRACTIONS_FACTOR_INT)

    @IgnoredOnParcel
    val majorDigits: List<Digit> by lazy { BigIntegers.digits(majorValue) }

    @IgnoredOnParcel
    val minorDigits: List<Digit> by lazy {
        val minorValue = value.mod(FRACTIONS_FACTOR_INT)
        BigIntegers.minorDigits(minorValue, FRACTIONS)
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
     * Returns if the current Quantity is in min & max boundaries
     */
    fun isValid(): Boolean = this in MIN_VALUE..MAX_VALUE

    override fun compareTo(other: Quantity): Int = value.compareTo(other.value)

    companion object {

        private val PRECISION: MathContext = MathContext.DECIMAL128
        const val FRACTIONS = 6

        private val FRACTIONS_FACTOR: BigDecimal = pow10decimal(FRACTIONS)
        private val FRACTIONS_FACTOR_INT: BigInteger = pow10(FRACTIONS)

        // MAX_VALUE for the Quantity class is 10 000
        val MAX_VALUE: Quantity = Quantity(BigInteger.valueOf(10000).multiply(FRACTIONS_FACTOR_INT))
        val MIN_VALUE: Quantity = MAX_VALUE.unaryMinus()
        val ZERO: Quantity = Quantity(BigInteger.ZERO)

        fun of(value: BigDecimal): Quantity =
            Quantity(value.multiply(FRACTIONS_FACTOR, PRECISION).toBigInteger())

        fun of(majorDigits: List<Digit>, fractionDigits: List<Digit>): Quantity =
            Quantity(BigIntegers.of(majorDigits, fractionDigits, FRACTIONS))
    }
}