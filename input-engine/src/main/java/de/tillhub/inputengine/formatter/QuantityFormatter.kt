package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.data.QuantityIO
import java.text.NumberFormat
import java.util.Locale

internal object QuantityFormatter {
    /**
     * Formats the given [quantity] to a [String]. No prefixes or suffixes are applied.
     */
    fun format(
        quantity: QuantityIO,
        minFractionDigits: Int = 0,
        locale: Locale = Locale.getDefault(Locale.Category.FORMAT)
    ): String {
        val format = NumberFormat.getNumberInstance(locale).apply {
            isGroupingUsed = true
            minimumFractionDigits = minFractionDigits
            maximumFractionDigits = QuantityIO.FRACTIONS
        }

        return format.format(quantity.decimal)
    }
}