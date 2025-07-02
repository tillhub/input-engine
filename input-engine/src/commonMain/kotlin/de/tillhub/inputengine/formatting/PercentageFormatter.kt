package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.data.PercentIO

expect class PercentageFormatter {
    fun format(
        percent: PercentIO,
    ): String
}
