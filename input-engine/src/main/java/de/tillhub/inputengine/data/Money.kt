package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

@Parcelize
data class Money(
    val value: BigDecimal,
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
        private val ZERO = 0.0.toBigDecimal()

        // MAX_VALUE_NORMAL for the Money class is 10 000 000 currency
        private val MAX_VALUE_DECIMAL: BigDecimal = 10000000.0.toBigDecimal()

        // MIN_VALUE_NORMAL for the Money class is 0 currency
        private val MIN_VALUE_DECIMAL: BigDecimal = 0.0.toBigDecimal()

        // Constructors
        fun zero() = Money(0.0.toBigDecimal())

        fun max() = Money(MAX_VALUE_DECIMAL)

        fun fromMinor(amount: BigInteger): Money {
            return Money(
                value = amount.toBigDecimal().movePointLeft(2),
            )
        }

        fun fromMinor(amount: BigDecimal): Money {
            return Money(
                value = amount.movePointLeft(2),
            )
        }

        fun toMinor(amount: Money): BigDecimal {
            return amount.value.movePointRight(2)
        }

        // Functions
        fun append(it: Money, digit: Digit): Money {
            val base = it.value.movePointRight(1)
            val digitMinorValue = digit.value.toBigDecimal().movePointLeft(2)
            val result = (base + digitMinorValue)
            val newValue = Money(result)
            return if (newValue.isValid()) {
                newValue
            } else {
                it
            }
        }

        fun removeLastDigit(it: Money): Money {
            val base = it.value.movePointLeft(1)
            val scale = base.setScale(2, RoundingMode.DOWN)
            return Money(scale)
        }
    }
}
