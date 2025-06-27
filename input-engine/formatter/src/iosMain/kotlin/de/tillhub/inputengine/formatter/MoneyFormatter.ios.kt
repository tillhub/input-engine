package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.MoneyIO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual object MoneyFormatter {
    actual fun format(
        money: MoneyIO,
        locale: String,
    ): String {
        val formatter = NSNumberFormatter().apply {
            this.locale = NSLocale(locale)
            numberStyle = NSNumberFormatterCurrencyStyle
            currencyCode = money.currency.code.value
        }

        val amountAsDouble = money.amount.toPlainString().toDouble()
        return formatter.stringFromNumber(NSNumber(double = amountAsDouble))
            ?: "${money.amount} ${money.currency}"
    }
}
