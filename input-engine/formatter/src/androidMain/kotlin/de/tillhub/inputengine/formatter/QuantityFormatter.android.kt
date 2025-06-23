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
        val decimal = quantity.getDecimal()

        // Define epsilon to round small scientific values like 0.0E-39 to zero
        val epsilon = com.ionspin.kotlin.bignum.decimal.BigDecimal.parseString("1E-6")

        // Normalize value to 0 if below epsilon threshold
        val normalized = if (decimal.abs() < epsilon) {
            com.ionspin.kotlin.bignum.decimal.BigDecimal.ZERO
        } else {
            decimal
        }

        return format.format(normalized.doubleValue(false))
    }
}