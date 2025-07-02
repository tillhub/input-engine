package de.tillhub.inputengine.data

import com.ionspin.kotlin.bignum.BigNumber
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import de.tillhub.inputengine.domain.serializer.BigDecimalSerializer
import de.tillhub.inputengine.domain.serializer.CurrencySerializer
import kotlinx.serialization.Serializable

/**
 * Money input/output which supports up to two decimal places
 * Int 100 -> 1,00 EUR
 * Double 100.0 -> 1,00 EUR
 */
@Serializable
class MoneyIO private constructor(
    @Serializable(with = BigDecimalSerializer::class) val amount: BigDecimal,
    @Serializable(with = CurrencySerializer::class) val currency: CurrencyIO,
) : Comparable<MoneyIO>, Number() {

    fun isZero() = amount.signum() == 0
    fun isNotZero() = !isZero()
    fun isNegative() = amount.signum() == -1
    fun isPositive(includingZero: Boolean = false) = if (includingZero) {
        amount.signum().let { it == 0 || it == 1 }
    } else {
        amount.signum() == 1
    }

    fun isValid(): Boolean = this <= MoneyIO(MAX_VALUE_DECIMAL, currency) &&
        this >= MoneyIO(MIN_VALUE_DECIMAL, currency)

    fun negate(): MoneyIO = MoneyIO(amount.negate(), currency)

    fun abs(): MoneyIO = MoneyIO(amount.abs(), currency)

    override fun compareTo(other: MoneyIO): Int = calculable(other) {
        amount.compareTo(other.amount)
    }

    operator fun unaryMinus() = MoneyIO(amount.negate(), currency)

    private fun <T> calculable(other: MoneyIO, body: () -> T): T {
        require(currency.code == other.currency.code) {
            "currency $currency differs from other currency: ${other.currency}"
        }
        return body()
    }

    override fun toByte(): Byte = amount.byteValue(exactRequired = false)

    override fun toShort(): Short =
        amount.moveDecimalPoint(currency.defaultFractionDigits).shortValue(exactRequired = false)

    override fun toInt(): Int =
        amount.moveDecimalPoint(currency.defaultFractionDigits).intValue(exactRequired = false)

    override fun toLong(): Long =
        amount.moveDecimalPoint(currency.defaultFractionDigits).longValue(exactRequired = false)

    override fun toFloat(): Float =
        amount.moveDecimalPoint(currency.defaultFractionDigits).floatValue(exactRequired = false)

    override fun toDouble(): Double =
        amount.moveDecimalPoint(currency.defaultFractionDigits).doubleValue(exactRequired = false)

    override fun toString() = "MoneyIO(amount=$amount, currency=$currency)"
    override fun equals(other: Any?) = other is MoneyIO &&
        amount == other.amount &&
        currency == other.currency

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + currency.hashCode()
        return result
    }

    companion object {

        // MAX_VALUE_NORMAL for the Money class is 10 000 000 currency
        private val MAX_VALUE_DECIMAL: BigDecimal = 10000000.0.toBigDecimal()

        // MIN_VALUE_NORMAL for the Money class is 0 currency
        private val MIN_VALUE_DECIMAL: BigDecimal = MAX_VALUE_DECIMAL.negate()

        // Constructors
        fun zero(currency: CurrencyIO) = MoneyIO(BigDecimal.ZERO, currency)
        fun max(currency: CurrencyIO) = MoneyIO(MAX_VALUE_DECIMAL, currency)
        fun min(currency: CurrencyIO) = MoneyIO(MIN_VALUE_DECIMAL, currency)

        fun of(amount: Number, currency: CurrencyIO): MoneyIO {
            return MoneyIO(
                when (amount) {
                    is Int -> amount.toBigDecimal()
                        .moveDecimalPoint(-currency.defaultFractionDigits)

                    is Long -> amount.toBigDecimal()
                        .moveDecimalPoint(-currency.defaultFractionDigits)

                    is Double -> amount.toBigDecimal()
                        .moveDecimalPoint(-currency.defaultFractionDigits)

                    else -> throw IllegalArgumentException("Money $amount is not supported type.")
                },
                currency,
            )
        }

        fun of(amount: BigNumber<*>, currency: CurrencyIO): MoneyIO {
            return MoneyIO(
                when (amount) {
                    is BigDecimal -> amount.moveDecimalPoint(-currency.defaultFractionDigits)
                    is BigInteger -> BigDecimal.fromBigInteger(amount)
                        .moveDecimalPoint(-currency.defaultFractionDigits)

                    else -> throw IllegalArgumentException("Money $amount is not supported type.")
                },
                currency,
            )
        }

        fun fromMajorUnits(amount: BigDecimal, currency: CurrencyIO): MoneyIO {
            return MoneyIO(amount, currency)
        }

        // Functions
        fun append(it: MoneyIO, digit: Int): MoneyIO {
            val base = it.amount.moveDecimalPoint(MOVE_ONE_RIGHT)
            val digitMinorValue =
                digit.toBigDecimal().moveDecimalPoint(-it.currency.defaultFractionDigits)
            val result =
                base.plus(if (it.isNegative()) digitMinorValue.negate() else digitMinorValue)
            val newValue = MoneyIO(result, it.currency)
            return if (newValue.isValid()) {
                newValue
            } else {
                it
            }
        }

        fun removeLastDigit(it: MoneyIO): MoneyIO {
            val base = it.amount.moveDecimalPoint(MOVE_ONE_LEFT)
            val scale = base.roundToDigitPositionAfterDecimalPoint(
                digitPosition = it.currency.defaultFractionDigits.toLong(),
                roundingMode = RoundingMode.TOWARDS_ZERO,
            )
            return MoneyIO(scale, it.currency)
        }

        const val MOVE_ONE_LEFT = -1
        const val MOVE_ONE_RIGHT = 1
    }
}
