package de.tillhub.inputengine.formatting

import com.ionspin.kotlin.bignum.decimal.toJavaBigDecimal
import de.tillhub.inputengine.data.MoneyIO
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

actual class MoneyFormatterImpl(
    private val locale: Locale = Locale.getDefault(),
) : MoneyFormatter {
    private val currencyFormat by lazy {
        NumberFormat.getCurrencyInstance(locale)
    }

    actual override fun format(money: MoneyIO): String = with(currencyFormat) {
        this.currency = Currency.getInstance(money.currency.isoCode)
        format(money.amount.toJavaBigDecimal())
    }
}
