package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.data.PercentIO
import java.text.NumberFormat
import java.util.Locale

internal object PercentageFormatter {

    fun format(
        percent: PercentIO,
        minimumFractionDigits: Int = 0,
        locale: Locale = Locale.getDefault(Locale.Category.FORMAT)
    ): String {
        val format = NumberFormat.getPercentInstance(locale).apply {
            this.minimumFractionDigits = minimumFractionDigits
            this.maximumFractionDigits = 2
        }

        return format.format(percent.toRatio())
    }
}