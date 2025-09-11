package de.tillhub.inputengine.domain

sealed class NumpadKey {
    data class SingleDigit(
        val digit: Digit,
    ) : NumpadKey()

    data object DecimalSeparator : NumpadKey()

    data object Negate : NumpadKey()

    data object Clear : NumpadKey()

    data object Delete : NumpadKey()
}
