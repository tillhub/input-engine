package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.MoneyIO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale

actual class MoneyFormatter(
    private val locale: NSLocale = NSLocale.currentLocale
) {

    private val currencyFormat by lazy {
        NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterCurrencyStyle
            locale = this@MoneyFormatter.locale
        }
    }

    actual fun format(money: MoneyIO): String {
        return with(currencyFormat) {
            currencyCode = money.currency.isoCode
            stringFromNumber(NSNumber(double = money.amount.doubleValue(exactRequired = false)))
        } ?: "${money.amount} ${money.currency}"
    }
}
