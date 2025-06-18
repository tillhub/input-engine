package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.QuantityIO

expect object QuantityFormatter {
    /**
     * Formats the given [quantity] to a [String]. No prefixes or suffixes are applied.
     */
    fun format(
        quantity: QuantityIO,
        minFractionDigits: Int = 0,
        locale: String
    ): String
}