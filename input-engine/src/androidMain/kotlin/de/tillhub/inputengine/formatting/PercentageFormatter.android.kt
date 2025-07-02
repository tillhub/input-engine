package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.PercentIO
import java.text.NumberFormat
import java.util.Locale

actual class PercentageFormatter(
    private val locale: Locale = Locale.getDefault()
) {
    private val formatter: NumberFormat by lazy {
        NumberFormat.getPercentInstance(locale).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 2
        }
    }

    actual fun format(
        percent: PercentIO,
    ): String = formatter.format(percent.toRatio())
}
