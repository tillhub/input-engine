package de.tillhub.inputengine.formatter

import java.text.DecimalFormatSymbols

actual object DecimalFormatter {
    private val symbols = DecimalFormatSymbols.getInstance()

    actual val decimalSeparator: Char = symbols.decimalSeparator
    actual val groupingSeparator: Char = symbols.groupingSeparator
}
