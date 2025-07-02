package de.tillhub.inputengine.formatting

import androidx.annotation.VisibleForTesting
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.decimalSeparator
import platform.Foundation.groupingSeparator

actual object DecimalFormatter {
    @VisibleForTesting
    val locale = NSLocale.currentLocale()

    actual val decimalSeparator: Char = locale.decimalSeparator.firstOrNull() ?: '.'
    actual val groupingSeparator: Char = locale.groupingSeparator.firstOrNull() ?: ','
}
