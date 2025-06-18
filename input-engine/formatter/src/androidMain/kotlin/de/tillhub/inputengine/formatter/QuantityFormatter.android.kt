package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.QuantityIO
import java.text.NumberFormat
import java.util.Locale

actual object QuantityFormatter {
    actual fun format(
        quantity: QuantityIO,
        minFractionDigits: Int,
        locale: String
    ): String {
        val localeObj = Locale.forLanguageTag(locale)
        val format = NumberFormat.getNumberInstance(localeObj).apply {
            isGroupingUsed = true
            minimumFractionDigits = minFractionDigits
            maximumFractionDigits = QuantityIO.FRACTIONS
        }
        return format.format(quantity.getDecimal())
    }
}