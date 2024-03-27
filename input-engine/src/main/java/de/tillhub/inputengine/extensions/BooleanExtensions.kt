package de.tillhub.inputengine.extensions

fun Boolean?.orTrue(): Boolean = this ?: true

fun Boolean?.orFalse(): Boolean = this ?: false
