package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.eur
import de.tillhub.inputengine.usd
import kotlinx.coroutines.test.runTest
import platform.Foundation.NSLocale
import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatterTest {
    @Test
    fun moneyFormatterTest() = runTest {
        val formatterDe = MoneyFormatterImpl(NSLocale(localeIdentifier = "de_DE"))
        val formatterUs = MoneyFormatterImpl(NSLocale(localeIdentifier = "en_US"))

        assertEquals("10,00 €", formatterDe.format(10.eur))
        assertEquals("10,00 $", formatterDe.format(10.usd))

        assertEquals("€10.00", formatterUs.format(10.eur))
        assertEquals("$10.00", formatterUs.format(10.usd))
    }
}
