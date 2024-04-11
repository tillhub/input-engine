package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.Currency

@Parcelize
data class Money(
    val value: BigDecimal,
    val currency: Currency
) : Comparable<Money>, Parcelable {

    fun isZero() = value.signum() == 0
    fun isNotZero() = !isZero()
    fun isNegative() = value.signum() < 0
    fun isPositive(includingZero: Boolean = false) = if (includingZero) {
        value.signum() >= 0
    } else {
        value.signum() > 0
    }

    fun isValid(): Boolean = this <= Money(MAX_VALUE_DECIMAL, currency) &&
            this >= Money(MIN_VALUE_DECIMAL, currency)

    override fun compareTo(other: Money): Int = calculable(other) {
        value.compareTo(other.value)
    }

    operator fun plus(other: Money) = calculable(other) {
        Money(value + other.value, currency)
    }

    operator fun minus(other: Money) = calculable(other) {
        Money(value - other.value, currency)
    }

    private fun <T> calculable(other: Money, body: () -> T): T {
        require(currency == other.currency) {
            "currency $currency differs from other currency: ${other.currency}"
        }
        return body()
    }

    companion object {

        // MAX_VALUE_NORMAL for the Money class is 10 000 000 currency
        private val MAX_VALUE_DECIMAL: BigDecimal = 10000000.0.toBigDecimal()

        // MIN_VALUE_NORMAL for the Money class is 0 currency
        private val MIN_VALUE_DECIMAL: BigDecimal = BigDecimal.ZERO

        // Constructors
        fun zero(currency: Currency) = Money(BigDecimal.ZERO, currency)
        fun max(currency: Currency) = Money(MAX_VALUE_DECIMAL, currency)
        fun min(currency: Currency) = Money(MIN_VALUE_DECIMAL, currency)

        fun from(amount: BigInteger, currency: Currency): Money {
            return Money(
                amount.toBigDecimal().movePointLeft(currency.defaultFractionDigits),
                currency
            )
        }

        // Functions
        fun append(it: Money, digit: Digit): Money {
            val base = it.value.movePointRight(1)
            val digitMinorValue =
                digit.value.toBigDecimal().movePointLeft(it.currency.defaultFractionDigits)
            val result = (base + digitMinorValue)
            val newValue = Money(result, it.currency)
            return if (newValue.isValid()) {
                newValue
            } else {
                it
            }
        }

        fun removeLastDigit(it: Money): Money {
            val base = it.value.movePointLeft(1)
            val scale = base.setScale(it.currency.defaultFractionDigits, RoundingMode.DOWN)
            return Money(scale, it.currency)
        }
    }
}
