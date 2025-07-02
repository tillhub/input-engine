package de.tillhub.inputengine.domain.helper

internal fun Char.isLatinLetter() =
    this in 'a'..'z' || this in 'A'..'Z'
