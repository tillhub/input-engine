package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.PercentIO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterPercentStyle
import platform.Foundation.currentLocale

actual class PercentageFormatter(
    private val locale: NSLocale = NSLocale.currentLocale,
) {
    private val formatter by lazy {
        NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterPercentStyle
            locale = this@PercentageFormatter.locale
            minimumFractionDigits = 0u
            maximumFractionDigits = 2u
        }
    }

    actual fun format(percent: PercentIO): String =
        formatter.stringFromNumber(NSNumber(percent.toRatio())) ?: "${percent.toRatio() * 100}%"
}
