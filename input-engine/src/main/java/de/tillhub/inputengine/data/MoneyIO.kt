package de.tillhub.inputengine.data

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.Currency
import java.util.Objects
/**
 * Money input/output which supports up to two decimal places
 * Int 100 -> 1,00 EUR
 * Double 100.0 -> 1,00 EUR
 */
class MoneyIO private constructor(
    val amount: BigDecimal,
    val currency: Currency
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

    operator fun unaryMinus() = MoneyIO(-amount, currency)

    private fun <T> calculable(other: MoneyIO, body: () -> T): T {
        require(currency == other.currency) {
            "currency $currency differs from other currency: ${other.currency}"
        }
        return body()
    }

    override fun toByte(): Byte = amount.toByte()

    override fun toDouble() = amount.movePointRight(currency.defaultFractionDigits).toDouble()

    override fun toFloat() = amount.movePointRight(currency.defaultFractionDigits).toFloat()

    override fun toInt() = amount.movePointRight(currency.defaultFractionDigits).toInt()

    override fun toLong() = amount.movePointRight(currency.defaultFractionDigits).toLong()

    override fun toShort() = amount.movePointRight(currency.defaultFractionDigits).toShort()

    override fun toString() = "MoneyIO(amount=$amount, currency=$currency)"
    override fun equals(other: Any?) = other is MoneyIO &&
            amount == other.amount &&
            currency == other.currency
    override fun hashCode() = Objects.hash(amount, currency)

    companion object {

        // MAX_VALUE_NORMAL for the Money class is 10 000 000 currency
        private val MAX_VALUE_DECIMAL: BigDecimal = 10000000.0.toBigDecimal()

        // MIN_VALUE_NORMAL for the Money class is 0 currency
        private val MIN_VALUE_DECIMAL: BigDecimal = -MAX_VALUE_DECIMAL

        // Constructors
        fun zero(currency: Currency) = MoneyIO(BigDecimal.ZERO, currency)
        fun max(currency: Currency) = MoneyIO(MAX_VALUE_DECIMAL, currency)
        fun min(currency: Currency) = MoneyIO(MIN_VALUE_DECIMAL, currency)

        fun of(amount: Number, currency: Currency): MoneyIO {
            return MoneyIO(
                when (amount) {
                    is Int -> amount.toBigDecimal().movePointLeft(currency.defaultFractionDigits)
                    is Long -> amount.toBigDecimal().movePointLeft(currency.defaultFractionDigits)
                    is Double -> amount.toBigDecimal().movePointLeft(currency.defaultFractionDigits)
                    is BigInteger -> amount.toBigDecimal().movePointLeft(currency.defaultFractionDigits)
                    is BigDecimal -> amount.movePointLeft(currency.defaultFractionDigits)
                    else -> throw IllegalArgumentException("Money $amount is not supported type.")
                },
                currency
            )
        }

        // Functions
        internal fun append(it: MoneyIO, digit: Digit): MoneyIO {
            val base = it.amount.movePointRight(1)
            val digitMinorValue =
                digit.value.toBigDecimal().movePointLeft(it.currency.defaultFractionDigits)
            val result = (base + if (it.isNegative()) digitMinorValue.negate() else digitMinorValue)
            val newValue = MoneyIO(result, it.currency)
            return if (newValue.isValid()) {
                newValue
            } else {
                it
            }
        }

        internal fun removeLastDigit(it: MoneyIO): MoneyIO {
            val base = it.amount.movePointLeft(1)
            val scale = base.setScale(it.currency.defaultFractionDigits, RoundingMode.DOWN)
            return MoneyIO(scale, it.currency)
        }
    }
}
