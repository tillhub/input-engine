package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.QuantityIO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.currentLocale
import platform.Foundation.numberWithDouble

actual class QuantityFormatterImpl(
    private val locale: NSLocale = NSLocale.currentLocale,
) : QuantityFormatter {
    private val formatter by lazy {
        NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            locale = this@QuantityFormatterImpl.locale
            minimumFractionDigits = 0u
            maximumFractionDigits = 2u
        }
    }

    actual override fun format(quantity: QuantityIO): String {
        val doubleValue = NSNumber.numberWithDouble(quantity.getDecimal().doubleValue(false))
        return formatter.stringFromNumber(doubleValue) ?: doubleValue.toString()
    }
}
