package de.tillhub.inputengine.data

import androidx.annotation.IntRange

@Suppress("MagicNumber")
internal enum class Digit(@IntRange(from = 0, to = 9) val value: Int) {
    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9);

    companion object {
        fun from(@IntRange(from = 0, to = 9) value: Int): Digit =
            entries.find {
                it.value == value
            } ?: throw IllegalArgumentException("Digit only supports from 0 to 9, but was: $value")
    }
}
