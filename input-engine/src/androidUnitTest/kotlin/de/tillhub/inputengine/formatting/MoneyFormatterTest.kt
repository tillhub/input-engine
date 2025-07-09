package de.tillhub.inputengine.formatting

import de.tillhub.inputengine.eur
import de.tillhub.inputengine.usd
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

class MoneyFormatterTest {

    @Test
    fun moneyFormatterTest() = runTest {
        val formatterDe = MoneyFormatter(Locale.GERMAN)
        val formatterUs = MoneyFormatter(Locale.US)

        assertEquals("10,00 €", formatterDe.format(10.eur))
        assertEquals("10,00 $", formatterDe.format(10.usd))

        assertEquals("€10.00", formatterUs.format(10.eur))
        assertEquals("$10.00", formatterUs.format(10.usd))
    }
}