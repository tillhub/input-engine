package de.tillhub.inputengine.helper

import de.tillhub.inputengine.financial.data.QuantityIO
import de.tillhub.inputengine.financial.data.QuantityIO.Companion.FRACTIONS
import de.tillhub.inputengine.financial.data.QuantityIO.Companion.FRACTIONS_FACTOR_INT

fun QuantityIO.getMajorDigits(): List<Digit> = DigitBuilder.digits(getMajorValue())
fun QuantityIO.getMinorDigits(): List<Digit> {
    val minorValue = value.mod(FRACTIONS_FACTOR_INT)
    return DigitBuilder.minorDigits(minorValue, FRACTIONS)
}
