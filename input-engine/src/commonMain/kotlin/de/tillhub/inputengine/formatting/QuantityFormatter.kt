package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.QuantityIO

interface QuantityFormatter {
    fun format(quantity: QuantityIO): String
}

expect class QuantityFormatterImpl : QuantityFormatter {
    /**
     * Formats the given [quantity] to a [String]. No prefixes or suffixes are applied.
     */
    override fun format(quantity: QuantityIO): String
}
