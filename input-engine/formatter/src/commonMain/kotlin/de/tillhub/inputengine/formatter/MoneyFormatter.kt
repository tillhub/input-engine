package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.financial.data.MoneyIO

expect object MoneyFormatter {
    fun format(
        money: MoneyIO,
        locale: String = defaultLocale()
    ): String
}