package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.MoneyIO
import de.tillhub.inputengine.helper.defaultLocale

expect object MoneyFormatter {
    fun format(
        money: MoneyIO,
        locale: String = defaultLocale(),
    ): String
}
