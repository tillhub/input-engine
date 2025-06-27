package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.PercentIO
import de.tillhub.inputengine.helper.defaultLocale

expect object PercentageFormatter {
    fun format(
        percent: PercentIO,
        minimumFractionDigits: Int = 0,
        locale: String = defaultLocale(),
    ): String
}
