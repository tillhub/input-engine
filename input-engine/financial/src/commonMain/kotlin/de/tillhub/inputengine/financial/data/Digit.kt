package de.tillhub.inputengine.financial.data

enum class Digit(val value: Int) {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    ;

    companion object {
        fun from(value: Int): Digit {
            require(value in 0..9) { "Digit only supports values from 0 to 9, but was: $value" }
            return entries.find { it.value == value }
                ?: throw IllegalArgumentException("Digit not found for value: $value")
        }
    }
}
