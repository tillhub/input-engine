package de.tillhub.inputengine.extensions

import java.util.Locale

fun Double.format(digits: Int, locale: Locale): String = "%.${digits}f".format(locale, this)
