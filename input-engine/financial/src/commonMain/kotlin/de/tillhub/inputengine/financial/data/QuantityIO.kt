package de.tillhub.inputengine.financial.data

import com.ionspin.kotlin.bignum.BigNumber
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.financial.helper.isPositive
import de.tillhub.inputengine.financial.helper.pow10decimal
import de.tillhub.inputengine.financial.helper.serializer.BigIntegerSerializer
import kotlinx.serialization.Serializable

/**
 * Quantity input/output which supports up to four decimal places
 * Int 1 -> x1 BigInteger(value=10000)
 * Double 1.56 -> x1,56 BigInteger(value=15600)
 * Float 1.2345F -> x1,2345 BigInteger(value=12345)
 */
@Serializable
class QuantityIO private constructor(
    @Serializable(with = BigIntegerSerializer::class) val value: BigInteger,
) : Comparable<QuantityIO>, Number() {

    fun getDecimal(): BigDecimal =
        BigDecimal.fromBigInteger(value).divide(FRACTIONS_FACTOR, PRECISION)

    fun getMajorValue(): BigInteger = value / FRACTIONS_FACTOR_INT

    fun hasFractions(): Boolean = (value % FRACTIONS_FACTOR_INT).isPositive(false)

    operator fun unaryMinus() = QuantityIO(value.negate())

    override fun compareTo(other: QuantityIO): Int = value.compareTo(other.value)

    override fun toByte(): Byte = getDecimal().byteValue(false)
    override fun toDouble(): Double = getDecimal().doubleValue(false)
    override fun toFloat(): Float = getDecimal().floatValue(false)
    override fun toInt(): Int = getMajorValue().intValue(false)
    override fun toLong(): Long = getMajorValue().longValue(false)
    override fun toShort(): Short = getMajorValue().shortValue(false)

    fun isPositive(includeZero: Boolean = false): Boolean = value.isPositive(includeZero)
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
    fun nextSmaller(
        allowsZero: Boolean = false,
        allowsNegatives: Boolean = false,
    ): QuantityIO =
        when (hasFractions()) {
            true -> when (isPositive(includeZero = false) && getMajorValue() != BigInteger.ZERO) {
                true -> getMajorValue().multiply(FRACTIONS_FACTOR_INT)
                false -> when (allowsNegatives && !allowsZero) {
                    true -> getMajorValue().minus(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                    false -> getMajorValue().multiply(FRACTIONS_FACTOR_INT)
                }
            }

            false -> when (allowsNegatives) {
                true -> when (allowsZero) {
                    true -> getMajorValue().minus(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                    false -> {
                        val tempValue = getMajorValue()
                            .minus(BigInteger.ONE)
                            .multiply(FRACTIONS_FACTOR_INT)
                        if (tempValue == BigInteger.ZERO) {
                            tempValue.minus(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT)
                        } else {
                            tempValue
                        }
                    }
                }

                false -> {
                    val lowerBound = if (allowsZero) BigInteger.ZERO else BigInteger.ONE
                    when (getMajorValue() == lowerBound) {
                        true -> value
                        false -> getMajorValue()
                            .minus(BigInteger.ONE)
                            .multiply(FRACTIONS_FACTOR_INT)
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
    fun nextLarger(allowsZero: Boolean = false, maxQuantity: QuantityIO = MAX_VALUE): QuantityIO =
        when (hasFractions()) {
            true -> when (allowsZero) {
                true -> {
                    val nextValue = QuantityIO(
                        value = getMajorValue().add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT),
                    )
                    if (nextValue <= maxQuantity) {
                        nextValue
                    } else {
                        QuantityIO(getMajorValue().multiply(FRACTIONS_FACTOR_INT))
                    }
                }

                false -> {
                    val nextValue = QuantityIO(
                        value = getMajorValue().add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT),
                    )
                    val tempValue =
                        if (isPositive()) {
                            if (nextValue <= maxQuantity) {
                                nextValue
                            } else {
                                this
                            }
                        } else {
                            QuantityIO(getMajorValue().multiply(FRACTIONS_FACTOR_INT))
                        }
                    if (tempValue.value == BigInteger.ZERO) {
                        QuantityIO(
                            tempValue.value.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT),
                        )
                    } else {
                        tempValue
                    }
                }
            }

            false -> when (allowsZero) {
                true -> {
                    val nextValue = QuantityIO(
                        value = getMajorValue().add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT),
                    )
                    if (nextValue <= maxQuantity) {
                        nextValue
                    } else {
                        QuantityIO(getMajorValue().multiply(FRACTIONS_FACTOR_INT))
                    }
                }

                false -> {
                    val nextValue = QuantityIO(
                        value = getMajorValue().add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT),
                    )
                    val tempValue = if (nextValue <= maxQuantity) {
                        nextValue
                    } else {
                        QuantityIO(getMajorValue().multiply(FRACTIONS_FACTOR_INT))
                    }
                    if (tempValue.value == BigInteger.ZERO) {
                        QuantityIO(
                            tempValue.value.add(BigInteger.ONE).multiply(FRACTIONS_FACTOR_INT),
                        )
                    } else {
                        tempValue
                    }
                }
            }
        }

    override fun toString() = "QuantityIO(value=$value)"
    override fun equals(other: Any?) = other is QuantityIO && value == other.value
    override fun hashCode() = value.hashCode()

    companion object {
        private val PRECISION: DecimalMode = DecimalMode(34, RoundingMode.ROUND_HALF_TO_EVEN)
        const val FRACTIONS = 4

        private val FRACTIONS_FACTOR: BigDecimal = pow10decimal(FRACTIONS)
        val FRACTIONS_FACTOR_INT: BigInteger = BigInteger.fromInt(10).pow(FRACTIONS)

        val MAX_VALUE: QuantityIO = QuantityIO(BigInteger.fromInt(10000) * FRACTIONS_FACTOR_INT)
        val MIN_VALUE: QuantityIO = QuantityIO(MAX_VALUE.value.negate())
        val ZERO: QuantityIO = QuantityIO(BigInteger.ZERO)

        fun of(number: Number): QuantityIO = QuantityIO(
            when (number) {
                is Int -> BigInteger.fromInt(number) * FRACTIONS_FACTOR_INT
                is Long -> BigInteger.fromLong(number) * FRACTIONS_FACTOR_INT
                is Double -> BigDecimal.fromDouble(number).multiply(FRACTIONS_FACTOR).floor()
                    .toBigInteger()

                is Float -> BigDecimal.fromFloat(number).multiply(FRACTIONS_FACTOR).floor()
                    .toBigInteger()

                else -> throw IllegalArgumentException("Not supported number for quantity")
            },
        )

        fun of(number: BigNumber<*>): QuantityIO = QuantityIO(
            when (number) {
                is BigInteger -> number.multiply(FRACTIONS_FACTOR_INT)
                is BigDecimal -> number.multiply(FRACTIONS_FACTOR, PRECISION).toBigInteger()
                else -> throw IllegalArgumentException("Percent $number is not supported type.")
            },
        )
    }
}
