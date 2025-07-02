package de.tillhub.inputengine.formatting

import com.ionspin.kotlin.bignum.decimal.toJavaBigDecimal
import de.tillhub.inputengine.data.QuantityIO
import java.text.NumberFormat
import java.util.Locale

actual class QuantityFormatter(
    private val locale: Locale = Locale.getDefault()
) {
    private val formatter: NumberFormat by lazy {
        NumberFormat.getInstance(locale).apply {
            isGroupingUsed = true
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
    }

    actual fun format(quantity: QuantityIO): String =
        formatter.format(quantity.getDecimal().toJavaBigDecimal())
}
