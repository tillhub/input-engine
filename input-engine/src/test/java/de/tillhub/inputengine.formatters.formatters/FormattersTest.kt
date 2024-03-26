package de.tillhub.inputengine.formatters.formatters

import de.tillhub.inputengine.formatter.MoneyFormatter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.Currency

class FormattersTest : FunSpec({

    test("Amount formatter") {
        val money = MoneyFormatter.format(100.toBigDecimal(), Currency.getInstance("EUR"))
        money shouldBe "€100.00"
    }
})