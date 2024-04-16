package de.tillhub.inputengine.formatter

import de.tillhub.inputengine.data.PercentIO
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.Locale

class PercentageFormatterTest : FunSpec({

    test("format: Int") {
        PercentageFormatter.format(
            PercentIO.ZERO, locale = Locale.GERMAN
        ) shouldBe "0 %"

        PercentageFormatter.format(
            PercentIO.of(7), locale = Locale.GERMAN
        ) shouldBe "7 %"

        PercentageFormatter.format(
            PercentIO.of(56), locale = Locale.GERMAN
        ) shouldBe "56 %"

        PercentageFormatter.format(
            PercentIO.of(63), locale = Locale.GERMAN
        ) shouldBe "63 %"

        PercentageFormatter.format(
            PercentIO.WHOLE, locale = Locale.GERMAN
        ) shouldBe "100 %"
    }

    test("format: Double") {
        PercentageFormatter.format(
            PercentIO.of(7.01), locale = Locale.GERMAN
        ) shouldBe "7,01 %"

        PercentageFormatter.format(
            PercentIO.of(34.0), locale = Locale.GERMAN
        ) shouldBe "34 %"

        PercentageFormatter.format(
            PercentIO.of(34.0), 1, locale = Locale.GERMAN
        ) shouldBe "34,0 %"

        PercentageFormatter.format(
            PercentIO.of(99.99), locale = Locale.GERMAN
        ) shouldBe "99,99 %"
    }
})
