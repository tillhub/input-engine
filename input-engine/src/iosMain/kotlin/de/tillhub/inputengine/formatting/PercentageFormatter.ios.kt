package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.PercentIO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterPercentStyle
import platform.Foundation.currentLocale

actual class PercentageFormatter(
    private val locale: NSLocale = NSLocale.currentLocale
) {
    actual fun format(percent: PercentIO): String {
        val formatter = NSNumberFormatter().apply {
            setLocale(locale)
            setNumberStyle(NSNumberFormatterPercentStyle)
            setMinimumFractionDigits(0u)
            setMaximumFractionDigits(2u)
        }
        return formatter.stringFromNumber(NSNumber(percent.toRatio())) ?: "${percent.toRatio() * 100}%"
    }
}
