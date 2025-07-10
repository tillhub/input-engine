package de.tillhub.inputengine.formatting

import com.ionspin.kotlin.bignum.decimal.toJavaBigDecimal
import de.tillhub.inputengine.data.MoneyIO
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

actual class MoneyFormatter(
    private val locale: Locale = Locale.getDefault(),
) {
    private val currencyFormat by lazy {
        NumberFormat.getCurrencyInstance(locale)
    }

    actual fun format(money: MoneyIO): String {
        return with(currencyFormat) {
            this.currency = Currency.getInstance(money.currency.isoCode)
            format(money.amount.toJavaBigDecimal())
        }
    }
}
