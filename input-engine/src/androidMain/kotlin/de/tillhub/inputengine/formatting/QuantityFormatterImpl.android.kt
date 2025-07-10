package de.tillhub.inputengine.formatting

import com.ionspin.kotlin.bignum.decimal.toJavaBigDecimal
import de.tillhub.inputengine.data.QuantityIO
import java.text.NumberFormat
import java.util.Locale

actual class QuantityFormatterImpl(
    private val locale: Locale = Locale.getDefault(),
) : QuantityFormatter {
    private val formatter: NumberFormat by lazy {
        NumberFormat.getInstance(locale).apply {
            isGroupingUsed = true
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
    }

    actual override fun format(quantity: QuantityIO): String = formatter.format(quantity.getDecimal().toJavaBigDecimal())
}
