package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.PercentIO
import java.text.NumberFormat
import java.util.Locale

actual object PercentageFormatter {
    actual fun format(
        percent: PercentIO,
        minimumFractionDigits: Int,
        locale: String,
    ): String {
        val localeObj = Locale.forLanguageTag(locale)
        val format = NumberFormat.getPercentInstance(localeObj).apply {
            this.minimumFractionDigits = minimumFractionDigits
            this.maximumFractionDigits = 2
        }
        return format.format(percent.toRatio())
    }
}
