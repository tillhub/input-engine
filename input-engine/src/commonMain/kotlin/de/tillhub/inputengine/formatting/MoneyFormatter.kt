package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.MoneyIO

interface MoneyFormatter {
    fun format(money: MoneyIO): String
}

expect class MoneyFormatterImpl : MoneyFormatter {
    override fun format(money: MoneyIO): String
}
