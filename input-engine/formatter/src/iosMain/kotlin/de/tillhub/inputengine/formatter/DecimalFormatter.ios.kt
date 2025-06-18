package de.tillhub.inputengine.formatter

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.decimalSeparator
import platform.Foundation.groupingSeparator

actual object DecimalFormatter {
    private val locale = NSLocale.currentLocale()

    actual val decimalSeparator: Char = locale.decimalSeparator.firstOrNull() ?: '.'
    actual val groupingSeparator: Char = locale.groupingSeparator.firstOrNull() ?: ','
}
