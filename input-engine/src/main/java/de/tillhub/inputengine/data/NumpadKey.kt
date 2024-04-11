package de.tillhub.inputengine.data

sealed class NumpadKey {
    data class SingleDigit(val digit: Digit) : NumpadKey()
    object DecimalSeparator : NumpadKey()

    data object Clear : NumpadKey()

    data object Delete : NumpadKey()
}
