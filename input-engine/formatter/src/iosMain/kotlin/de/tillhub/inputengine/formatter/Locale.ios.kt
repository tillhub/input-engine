package de.tillhub.inputengine.formatter

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier

actual fun defaultLocale(): String {
    return NSLocale.currentLocale.localeIdentifier
        .substringBefore("@")   // Strip Unicode extensions like "@rg=dezzzz"
        .replace("_", "-")      // Normalize format to match Android's BCP-47
}
