package de.tillhub.inputengine.data

import android.os.Parcelable
import androidx.annotation.Keep
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

/**
 * Quantity input/output which supports up to two decimal places
 * Int 1 -> x1 BigInteger(value=100)
 * Double 1.56 -> x1,56 BigInteger(value=156)
 */
@Parcelize
data class QuantityIO internal constructor(
    val value: BigInteger
) : Parcelable, Comparable<QuantityIO>, Number() {

    @IgnoredOnParcel
    val decimal: BigDecimal by lazy {
        value.toBigDecimal().divide(FRACTIONS_FACTOR, PRECISION)
    }

    @IgnoredOnParcel
    val majorValue: BigInteger = value.divide(FRACTIONS_FACTOR_INT)

    @IgnoredOnParcel
    internal val majorDigits: List<Digit> by lazy { BigIntegers.digits(majorValue) }

    @IgnoredOnParcel
    internal val minorDigits: List<Digit> by lazy {
        val minorValue = value.mod(FRACTIONS_FACTOR_INT)
        BigIntegers.minorDigits(minorValue, FRACTIONS)
    }

    @IgnoredOnParcel
    val hasFractions = value.mod(FRACTIONS_FACTOR_INT).isPositive(includeZero = false)

    operator fun unaryMinus() = QuantityIO(-value)

    operator fun plus(other: QuantityIO): QuantityIO = QuantityIO(value.add(other.value))
    operator fun minus(other: QuantityIO): QuantityIO = QuantityIO(value.subtract(other.value))

    operator fun times(other: QuantityIO): QuantityIO =
        QuantityIO(value.multiply(other.value).divide(FRACTIONS_FACTOR_INT))

    operator fun div(other: QuantityIO): QuantityIO = of(
        value.toBigDecimal().divide(other.value.toBigDecimal(), FRACTIONS, RoundingMode.HALF_UP)
    )

    override fun compareTo(other: QuantityIO): Int = value.compareTo(other.value)

    override fun toByte(): Byte = value.toByte()

    override fun toDouble(): Double = decimal.toDouble()

    override fun toFloat(): Float = decimal.toFloat()

    override fun toInt(): Int = value.divide(FRACTIONS_FACTOR_INT).toInt()

    override fun toLong(): Long = value.divide(FRACTIONS_FACTOR_INT).toLong()

    override fun toShort(): Short = value.divide(FRACTIONS_FACTOR_INT).toShort()

    private fun isPositive(includeZero: Boolean = false): Boolean = value.isPositive(includeZero)
    fun isNegative() = value.signum() == -1
    fun isZero(): Boolean = value.isZero()

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
    internal fun nextSmaller(allowsZero: Boolean = false, allowsNegatives: Boolean = false): QuantityIO =
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
        }.let { QuantityIO(it) }

    /**
     * Returns the next larger quantity amount. In case the current quantity has fraction values the result is
     * rounded up to the next larger integer value.
     *
     * i.e.
     * - 4.5 returns 5.0
     * - 4.0 returns 5.0
     * - 1.001 returns 2.0
     */
    @Suppress("NestedBlockDepth", "LongMethod")
    internal fun nextLarger(allowsZero: Boolean = false, maxQuantity: QuantityIO = MAX_VALUE): QuantityIO =
        when (hasFractions) {
            true -> when (allowsZero) {
                true -> {
                    val nextValue =
                        QuantityIO(majorValue.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT))
                    if (nextValue <= maxQuantity) {
                        nextValue
                    } else {
                        QuantityIO(majorValue.multiply(FRACTIONS_FACTOR_INT))
                    }
                }

                false -> {
                    val nextValue =
                        QuantityIO(majorValue.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT))
                    val tempValue =
                        if (isPositive()) {
                            if (nextValue <= maxQuantity) {
                                nextValue
                            } else {
                                this
                            }
                        } else {
                            QuantityIO(majorValue.multiply(FRACTIONS_FACTOR_INT))
                        }
                    if (tempValue.value == BigInteger.ZERO) {
                        QuantityIO(
                            tempValue.value.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                        )
                    } else {
                        tempValue
                    }
                }
            }

            false -> when (allowsZero) {
                true -> {
                    val nextValue =
                        QuantityIO(majorValue.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT))
                    if (nextValue <= maxQuantity) {
                        nextValue
                    } else {
                        QuantityIO(majorValue.multiply(FRACTIONS_FACTOR_INT))
                    }
                }

                false -> {
                    val nextValue =
                        QuantityIO(majorValue.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT))
                    val tempValue = if (nextValue <= maxQuantity) {
                        nextValue
                    } else {
                        QuantityIO(majorValue.multiply(FRACTIONS_FACTOR_INT))
                    }
                    if (tempValue.value == BigInteger.ZERO) {
                        QuantityIO(
                            tempValue.value.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                        )
                    } else {
                        tempValue
                    }
                }
            }
        }

    companion object {

        private val PRECISION: MathContext = MathContext.DECIMAL128
        internal const val FRACTIONS = 2

        private val FRACTIONS_FACTOR: BigDecimal = pow10decimal(FRACTIONS)
        private val FRACTIONS_FACTOR_INT: BigInteger = pow10(FRACTIONS)

        // MAX_VALUE for the Quantity class is 10 000
        val MAX_VALUE: QuantityIO =
            QuantityIO(BigInteger.valueOf(10000).multiply(FRACTIONS_FACTOR_INT))
        val MIN_VALUE: QuantityIO = MAX_VALUE.unaryMinus()
        val ZERO: QuantityIO = QuantityIO(BigInteger.ZERO)

        fun of(number: Number): QuantityIO = QuantityIO(when (number) {
            is Int -> number.toBigInteger().multiply(FRACTIONS_FACTOR_INT)
            is Long -> number.toBigInteger().multiply(FRACTIONS_FACTOR_INT)
            is Double -> number.toBigDecimal().multiply(FRACTIONS_FACTOR, PRECISION).toBigInteger()
            is Float -> number.toBigDecimal().multiply(FRACTIONS_FACTOR, PRECISION).toBigInteger()
            is BigDecimal -> number.multiply(FRACTIONS_FACTOR, PRECISION).toBigInteger()
            is BigInteger -> number.multiply(FRACTIONS_FACTOR_INT)
            else -> throw IllegalArgumentException("Not supported number for quantity")
        })
    }
}