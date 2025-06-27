package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.MoneyIO
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import java.math.BigDecimal as JvmBigDecimal

actual object MoneyFormatter {
    actual fun format(
        money: MoneyIO,
        locale: String,
    ): String {
        val localeObj = Locale.forLanguageTag(locale)
        val format = NumberFormat.getCurrencyInstance(localeObj).apply {
            this.currency = Currency.getInstance(money.currency.code.value)
        }
        val jvmBigDecimal = JvmBigDecimal(money.amount.toPlainString())
        return format.format(jvmBigDecimal)
    }
}
