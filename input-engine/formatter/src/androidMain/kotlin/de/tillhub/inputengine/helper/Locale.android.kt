package de.tillhub.inputengine.helper

import java.util.Locale

actual fun defaultLocale(): String {
    return Locale.getDefault(Locale.Category.FORMAT).toLanguageTag()
}