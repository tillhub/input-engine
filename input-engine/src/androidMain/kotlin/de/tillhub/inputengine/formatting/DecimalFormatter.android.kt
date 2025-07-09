package de.tillhub.inputengine.formatting

import androidx.annotation.VisibleForTesting
import java.text.DecimalFormatSymbols
import java.util.Locale

actual object DecimalFormatter {
    @VisibleForTesting
    var locale: Locale? = null

    actual val decimalSeparator: Char
        get() = DecimalFormatSymbols.getInstance(locale ?: Locale.getDefault()).decimalSeparator

    actual val groupingSeparator: Char
        get() = DecimalFormatSymbols.getInstance(locale ?: Locale.getDefault()).groupingSeparator
}
