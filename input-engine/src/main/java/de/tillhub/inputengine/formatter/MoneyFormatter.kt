package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.data.Money
import java.math.BigInteger
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object MoneyFormatter {

    fun format(
        money: Money,
        locale: Locale = Locale.getDefault(Locale.Category.FORMAT)
    ): String {
        val format = NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = money.currency
        }
        return format.format(money.value)
    }

    fun format(
        amount: BigInteger,
        currency: Currency,
        locale: Locale = Locale.getDefault(Locale.Category.FORMAT)
    ): String {
        return format(Money.from(amount, currency), locale)
    }
}