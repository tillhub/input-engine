package de.tillhub.inputengine.data

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger

/**
 * A percentage values which supports up to two decimal places. i.e. 15.12%
 * Int 56 -> 56%  Long(value=5600L)
 * Double 5.6 -> 5,6%   Long(value=560L)
 */
@Parcelize
data class PercentIO internal constructor(
    val value: Long
) : Parcelable, Comparable<PercentIO>, Number() {

    override fun compareTo(other: PercentIO): Int = value.compareTo(other.value)

    override fun toByte(): Byte = value.toByte()

    override fun toDouble(): Double = value.toDouble() / WHOLE_VALUE.toDouble()

    override fun toFloat(): Float = value.toFloat() / WHOLE_VALUE

    override fun toInt(): Int = (value / I_100).toInt()

    override fun toLong(): Long = value / I_100

    override fun toShort(): Short = (value / I_100).toShort()

    companion object {
        private const val ZERO_VALUE = 0L
        private const val I_100 = 100L
        private const val WHOLE_VALUE = 10000L

        /**
         * Represents 100%
         */
        @Keep val WHOLE: PercentIO = PercentIO(WHOLE_VALUE)

        /**
         * Represents 0%
         */
        @Keep val ZERO: PercentIO = PercentIO(ZERO_VALUE)

        @Keep
        fun of(number: Number): PercentIO {
            return PercentIO(when (number) {
                is Int -> number * I_100
                is Long -> number * I_100
                is Double -> (number * I_100).toLong()
                is BigInteger -> number.toLong() * I_100
                is BigDecimal -> number.multiply(I_100.toBigDecimal()).toLong()
                else -> throw IllegalArgumentException("Percentage not supported number type")
            })
        }
    }
}