package de.tillhub.inputengine.formatter

import java.util.Locale

actual fun defaultLocale(): String {
    return Locale.getDefault(Locale.Category.FORMAT).toLanguageTag()
}