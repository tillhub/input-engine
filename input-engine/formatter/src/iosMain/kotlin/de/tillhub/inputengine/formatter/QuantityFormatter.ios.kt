package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.QuantityIO

actual object QuantityFormatter {
    actual fun format(
        quantity: QuantityIO,
        minFractionDigits: Int,
        locale: String
    ): String {
       return ""
    }
}