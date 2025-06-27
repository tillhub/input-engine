package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.PercentIO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterPercentStyle

actual object PercentageFormatter {
    actual fun format(
        percent: PercentIO,
        minimumFractionDigits: Int,
        locale: String,
    ): String {
        val formatter = NSNumberFormatter().apply {
            setLocale(NSLocale(locale))
            setNumberStyle(NSNumberFormatterPercentStyle)
            setMinimumFractionDigits(minimumFractionDigits.toULong())
            setMaximumFractionDigits(2u)
        }
        return formatter.stringFromNumber(NSNumber(percent.toRatio())) ?: "${percent.toRatio() * 100}%"
    }
}
