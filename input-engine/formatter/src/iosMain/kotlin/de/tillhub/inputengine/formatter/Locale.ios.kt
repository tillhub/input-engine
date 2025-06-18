package de.tillhub.inputengine.formatter

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier

actual fun defaultLocale(): String {
    return NSLocale.currentLocale.localeIdentifier.replace("_", "-")
}