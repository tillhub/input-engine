package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.MoneyIO
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale

actual class MoneyFormatterImpl(
    private val locale: NSLocale = NSLocale.currentLocale,
) : MoneyFormatter {
    private val currencyFormat by lazy {
        NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterCurrencyStyle
            locale = this@MoneyFormatterImpl.locale
        }
    }

    actual override fun format(money: MoneyIO): String = with(currencyFormat) {
        currencyCode = money.currency.isoCode
        stringFromNumber(NSNumber(double = money.amount.doubleValue(exactRequired = false)))
    } ?: "${money.amount} ${money.currency}"
}
