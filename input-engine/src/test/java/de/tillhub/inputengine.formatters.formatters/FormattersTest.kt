package de.tillhub.inputengine.formatters.formatters

import de.tillhub.inputengine.formatter.MoneyFormatter
import de.tillhub.inputengine.helper.eur
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.Locale

class FormattersTest : DescribeSpec({

    describe("Amount formatter") {

        it("amount with big decimal") {
            MoneyFormatter.format(
                1.0.toBigDecimal().eur,
                Locale.GERMAN
            ) shouldBe "1,00 €"
        }

        it("amount with big integer") {
            MoneyFormatter.format(
                100.toBigInteger().eur,
                Locale.GERMAN
            ) shouldBe "1,00 €"
        }

        it("zero with big decimal") {
            MoneyFormatter.format(
                0.0.toBigDecimal().eur,
                Locale.GERMAN
            ) shouldBe "0,00 €"
        }
        it("zero with big integer") {
            MoneyFormatter.format(
                0.toBigInteger().eur,
                Locale.GERMAN
            ) shouldBe "0,00 €"
        }
    }
})
