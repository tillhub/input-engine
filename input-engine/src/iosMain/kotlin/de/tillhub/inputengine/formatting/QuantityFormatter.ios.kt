package de.tillhub.inputengine.formatting

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.data.QuantityIO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.currentLocale
import platform.Foundation.numberWithDouble

actual class QuantityFormatter(
    private val locale: NSLocale = NSLocale.currentLocale
) {
    actual fun format(quantity: QuantityIO): String {
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
            locale = this@QuantityFormatter.locale
            numberStyle = NSNumberFormatterDecimalStyle
            usesGroupingSeparator = true
            minimumFractionDigits = 0.toULong()
            maximumFractionDigits = QuantityIO.FRACTIONS.toULong()
        }

        return numberFormatter.stringFromNumber(NSNumber.numberWithDouble(doubleValue)) ?: doubleValue.toString()
    }
}
