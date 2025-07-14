package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.PercentIO

fun interface PercentageFormatter {
    fun format(percent: PercentIO): String
}

expect class PercentageFormatterImpl : PercentageFormatter {
    override fun format(percent: PercentIO): String
}
