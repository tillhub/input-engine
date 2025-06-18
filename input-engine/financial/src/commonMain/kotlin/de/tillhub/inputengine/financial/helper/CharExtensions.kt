package de.tillhub.inputengine.financial.helper

internal fun Char.isLatinLetter() =
	this in 'a' .. 'z' || this in 'A' .. 'Z'