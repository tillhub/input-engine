package de.tillhub.helper

import de.tillhub.inputengine.helper.defaultLocale
import kotlin.test.Test
import kotlin.test.assertTrue

class LocaleTest {

    @Test
    fun testDefaultLocaleNotBlank() {
        val locale = defaultLocale()
        assertTrue(locale.isNotBlank(), "Locale should not be blank")
    }

    @Test
    fun testDefaultLocaleFormat() {
        val locale = defaultLocale()
        val regex = Regex("""^[a-z]{2}([-_][A-Z]{2})?$""")
        assertTrue(regex.matches(locale), "Unexpected locale format: $locale")
    }
}
