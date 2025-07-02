package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.MoneyIO

expect class MoneyFormatter {
    fun format(money: MoneyIO): String
}
