package de.tillhub.inputengine.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

@Parcelize
@JvmInline
value class Amount private constructor(
    val value: BigDecimal,
) : Comparable<Amount>, Parcelable {

    fun isZero() = value == BigDecimal.ZERO
    fun isNotZero() = !isZero()
    fun isNegative() = value < BigDecimal.ZERO
    fun isPositive(includingZero: Boolean = false) = if (includingZero) {
        value >= BigDecimal.ZERO
    } else {
        value > BigDecimal.ZERO
    }

    fun isValid(): Boolean = this <= Amount(MAX_VALUE_DECIMAL) &&
            this >= Amount(MIN_VALUE_DECIMAL)

    override fun compareTo(other: Amount): Int =
        value.compareTo(other.value)

    operator fun plus(other: Amount) =
        Amount(value + other.value)

    operator fun minus(other: Amount) =
        Amount(value - other.value)

    companion object {

        // MAX_VALUE_NORMAL for the Money class is 10 000 000 currency
        private val MAX_VALUE_DECIMAL: BigDecimal = 10000000.0.toBigDecimal()

        // MIN_VALUE_NORMAL for the Money class is 0 currency
        private val MIN_VALUE_DECIMAL: BigDecimal = BigDecimal.ZERO

        // Constructors
        fun zero() = Amount(BigDecimal.ZERO)
        fun max() = Amount(MAX_VALUE_DECIMAL)
        fun min() = Amount(MIN_VALUE_DECIMAL)

        fun from(amount: BigInteger): Amount {
            return Amount(amount.toBigDecimal())
        }

        fun from(amount: BigDecimal): Amount {
            return Amount(amount)
        }

        // Functions
        fun append(it: Amount, digit: Digit): Amount {
            val base = it.value.movePointRight(1)
            val digitMinorValue = digit.value.toBigDecimal().movePointLeft(2)
            val result = (base + digitMinorValue)
            val newValue = Amount(result)
            return if (newValue.isValid()) {
                newValue
            } else {
                it
            }
        }

        fun removeLastDigit(it: Amount): Amount {
            val base = it.value.movePointLeft(1)
            val scale = base.setScale(2, RoundingMode.DOWN)
            return Amount(scale)
        }
    }
}
