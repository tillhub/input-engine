package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.data.MoneyIO
import java.text.NumberFormat
import java.util.Locale

internal object MoneyFormatter {

    fun format(
        money: MoneyIO,
        locale: Locale = Locale.getDefault(Locale.Category.FORMAT)
    ): String {
        val format = NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = money.currency
        }
        return format.format(money.amount)
    }
}