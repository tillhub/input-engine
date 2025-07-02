package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.QuantityIO

expect class QuantityFormatter {
    /**
     * Formats the given [quantity] to a [String]. No prefixes or suffixes are applied.
     */
    fun format(quantity: QuantityIO): String
}
