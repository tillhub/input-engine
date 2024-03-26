package de.tillhub.inputengine.formatter

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object MoneyFormatter {

    fun format(
        amount: BigDecimal,
        currency: Currency,
        locale: Locale = Locale.getDefault(Locale.Category.FORMAT)
    ): String {
        return NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = currency
        }.format(amount)
    }
}
