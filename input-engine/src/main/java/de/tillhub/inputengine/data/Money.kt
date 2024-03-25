package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

@Parcelize
data class Money(
    val value: Double,
) : Comparable<Money>, Parcelable {

    fun isZero() = value == ZERO
    fun isNotZero() = !isZero()
    fun isNegative() = value < ZERO
    fun isPositive(includingZero: Boolean = false) = if (includingZero) {
        value >= ZERO
    } else {
        value > ZERO
    }

    fun isValid(): Boolean = this <= Money(MAX_VALUE_DECIMAL) &&
            this >= Money(MIN_VALUE_DECIMAL)

    override fun compareTo(other: Money): Int =
        value.compareTo(other.value)

    operator fun plus(other: Money) =
        Money(value + other.value)

    operator fun minus(other: Money) =
        Money(value - other.value)

    companion object {
        private const val ZERO = 0.0

        // MAX_VALUE_NORMAL for the Money class is 10 000 000 currency
        private const val MAX_VALUE_DECIMAL: Double = 10000000.0

        // MIN_VALUE_NORMAL for the Money class is 0 currency
        private const val MIN_VALUE_DECIMAL: Double = 0.0

        // Constructors
        fun zero() = Money(0.00)

        fun max() = Money(MAX_VALUE_DECIMAL)

        fun fromMinor(amount: BigInteger): Money {
            return Money(
                value = amount.toBigDecimal().movePointLeft(2).toDouble(),
            )
        }

        fun fromMinor(amount: BigDecimal): Money {
            return Money(
                value = amount.movePointLeft(2).toDouble(),
            )
        }

        fun toMinor(amount: Money): BigDecimal {
            return amount.value.toBigDecimal().movePointRight(2)
        }

        // Functions
        fun append(it: Money, digit: Digit): Money {
            val base = it.value.toBigDecimal().movePointRight(1)
            val digitMinorValue = digit.value.toBigDecimal().movePointLeft(2)
            val result = (base + digitMinorValue).toDouble()
            val newValue = Money(result)
            return if (newValue.isValid()) {
                newValue
            } else {
                it
            }
        }

        fun removeLastDigit(it: Money): Money {
            val base = it.value.toBigDecimal().movePointLeft(1)
            val scale = base.setScale(2, RoundingMode.DOWN)
            return Money(scale.toDouble())
        }
    }
}
