package de.tillhub.inputengine.financial.data

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.financial.helper.isPositive
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
    @Serializable(with = BigIntegerSerializer::class) val value: BigInteger
) : Comparable<QuantityIO>, Number() {

    fun getDecimal(): BigDecimal = BigDecimal.fromBigInteger(value).divide(FRACTIONS_FACTOR)

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

    internal fun nextSmaller(
        allowsZero: Boolean = false,
        allowsNegatives: Boolean = false
    ): QuantityIO {
        val result: BigInteger = when (hasFractions()) {
            true -> when {
                isPositive(false) && getMajorValue() != BigInteger.ZERO ->
                    getMajorValue() * FRACTIONS_FACTOR_INT

                allowsNegatives && !allowsZero ->
                    (getMajorValue() - BigInteger.ONE) * FRACTIONS_FACTOR_INT

                else ->
                    getMajorValue() * FRACTIONS_FACTOR_INT
            }

            false -> when {
                allowsNegatives -> {
                    if (allowsZero) {
                        (getMajorValue() - BigInteger.ONE) * FRACTIONS_FACTOR_INT
                    } else {
                        val tempValue = getMajorValue() * FRACTIONS_FACTOR_INT
                        if (tempValue == BigInteger.ZERO) tempValue else tempValue - BigInteger.ONE
                    }
                }

                else -> {
                    val lowerBound = if (allowsZero) BigInteger.ZERO else BigInteger.ONE
                    if (getMajorValue() == lowerBound) {
                        value
                    } else {
                        (getMajorValue() - BigInteger.ONE) * FRACTIONS_FACTOR_INT
                    }
                }
            }
        }
        return QuantityIO(result)
    }

    internal fun nextLarger(allowsZero: Boolean = false, maxQuantity: QuantityIO = MAX_VALUE): QuantityIO =
        when (hasFractions()) {
            true, false -> {
                val nextValue = QuantityIO((getMajorValue() + BigInteger.ONE) * FRACTIONS_FACTOR_INT)
                val tempValue = if (allowsZero) {
                    if (nextValue <= maxQuantity) nextValue else QuantityIO(getMajorValue() * FRACTIONS_FACTOR_INT)
                } else {
                    if (isPositive() && nextValue <= maxQuantity) nextValue
                    else QuantityIO(getMajorValue() * FRACTIONS_FACTOR_INT)
                }
                if (tempValue.value == BigInteger.ZERO && !allowsZero) {
                    QuantityIO(BigInteger.ONE * FRACTIONS_FACTOR_INT)
                } else {
                    tempValue
                }
            }
        }

    override fun toString() = "QuantityIO(value=$value)"
    override fun equals(other: Any?) = other is QuantityIO && value == other.value
    override fun hashCode() = value.hashCode()

    companion object {
        const val FRACTIONS = 4

        private val FRACTIONS_FACTOR: BigDecimal = BigDecimal.fromInt(10).pow(FRACTIONS)
        val FRACTIONS_FACTOR_INT: BigInteger = BigInteger.fromInt(10).pow(FRACTIONS)

        val MAX_VALUE: QuantityIO = QuantityIO(BigInteger.fromInt(10000) * FRACTIONS_FACTOR_INT)
        val MIN_VALUE: QuantityIO = QuantityIO(MAX_VALUE.value.negate())
        val ZERO: QuantityIO = QuantityIO(BigInteger.ZERO)

        fun of(number: Number): QuantityIO = QuantityIO(
            when (number) {
                is Int -> BigInteger.fromInt(number) * FRACTIONS_FACTOR_INT
                is Long -> BigInteger.fromLong(number) * FRACTIONS_FACTOR_INT
                is Double -> BigDecimal.fromDouble(number).multiply(FRACTIONS_FACTOR).floor().toBigInteger()
                is Float -> BigDecimal.fromFloat(number).multiply(FRACTIONS_FACTOR).floor().toBigInteger()
                else -> throw IllegalArgumentException("Not supported number for quantity")
            }
        )
    }
}

