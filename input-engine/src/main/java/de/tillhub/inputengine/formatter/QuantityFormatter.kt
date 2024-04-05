package de.tillhub.inputengine.formatter

import android.icu.text.NumberFormat
import de.tillhub.inputengine.data.Quantity
import java.text.DecimalFormatSymbols

object QuantityFormatter {
    private fun getFormatter(minFractionDigits: Int = 0): NumberFormat =
        NumberFormat.getInstance().apply {
            isGroupingUsed = true
            minimumFractionDigits = minFractionDigits
            maximumFractionDigits = Quantity.FRACTIONS
        }

    fun getDecimalSeparator(): Char = DecimalFormatSymbols.getInstance().decimalSeparator

    /**
     * Formats the given [count] to a [String]. No prefixes or suffixes are applied.
     */
    // TODO add unit tests
    fun formatPlain(count: Long): String = getFormatter().format(count)

    /**
     * Formats the given [quantity] to a [String]. No prefixes or suffixes are applied.
     */
    // TODO add unit tests
    fun formatPlain(quantity: Quantity, minFractionDigits: Int = 0): String =
        getFormatter(minFractionDigits = minFractionDigits).format(quantity.decimal)

    /**
     * Formats the given [count] in the form of "x8" for a given value of 8
     */
    fun format(count: Quantity): String = formatPlain(count).let { "x$it" }

    /**
     * Formats the given [quantity] as a prefix with an optional suffix. The result is in the form of "8 x [suffix]"
     * for a given value of 8.
     */
    fun formatAsPrefix(quantity: Quantity, suffix: String = ""): String =
        formatPlain(quantity).let { "$it x $suffix" }
}