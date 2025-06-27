package de.tillhub.inputengine.formatter

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.financial.data.QuantityIO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.localeWithLocaleIdentifier
import platform.Foundation.numberWithDouble

actual object QuantityFormatter {
    actual fun format(
        quantity: QuantityIO,
        minFractionDigits: Int,
        locale: String,
    ): String {
        val decimal = quantity.getDecimal()

        // Define epsilon as 1E-6
        val epsilon = BigDecimal.parseString("1E-6")

        // Normalize values smaller than epsilon to 0
        val normalized = if (decimal.abs() < epsilon) {
            BigDecimal.ZERO
        } else {
            decimal
        }

        val doubleValue = normalized.doubleValue(false)

        val numberFormatter = NSNumberFormatter().apply {
            this.locale = NSLocale.localeWithLocaleIdentifier(locale)
            numberStyle = NSNumberFormatterDecimalStyle
            usesGroupingSeparator = true
            minimumFractionDigits = minFractionDigits.toULong()
            maximumFractionDigits = QuantityIO.FRACTIONS.toULong()
        }

        return numberFormatter.stringFromNumber(NSNumber.numberWithDouble(doubleValue)) ?: doubleValue.toString()
    }
}
