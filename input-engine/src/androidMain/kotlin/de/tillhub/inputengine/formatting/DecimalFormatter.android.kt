package de.tillhub.inputengine.formatting

import java.text.DecimalFormatSymbols
import java.util.Locale

actual object DecimalFormatter {
    actual val decimalSeparator: Char
        get() = DecimalFormatSymbols.getInstance(Locale.getDefault()).decimalSeparator

    actual val groupingSeparator: Char
        get() = DecimalFormatSymbols.getInstance(Locale.getDefault()).groupingSeparator
}
