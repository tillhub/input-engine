package de.tillhub.inputengine.formatter

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.tillhub.inputengine.financial.data.QuantityIO
import java.text.NumberFormat
import java.util.Locale

actual object QuantityFormatter {

    private val EPSILON = BigDecimal.parseString("1E-6")

    actual fun format(
        quantity: QuantityIO,
        minFractionDigits: Int,
        locale: String
    ): String {
        val localeObj = Locale.forLanguageTag(locale)
        val format = NumberFormat.getNumberInstance(localeObj)
        format.isGroupingUsed = true
        format.minimumFractionDigits = minFractionDigits
        format.maximumFractionDigits = QuantityIO.FRACTIONS

        val decimal = quantity.getDecimal()
        val normalized = if (decimal.abs() < EPSILON) BigDecimal.ZERO else decimal

        return format.format(normalized.doubleValue(exactRequired = false))
    }
}