package de.tillhub.inputengine.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DigitTest : FunSpec({

    test("from") {
        Digit.from(0) shouldBe Digit.ZERO
        Digit.from(1) shouldBe Digit.ONE
        Digit.from(2) shouldBe Digit.TWO
        Digit.from(3) shouldBe Digit.THREE
        Digit.from(4) shouldBe Digit.FOUR
        Digit.from(5) shouldBe Digit.FIVE
        Digit.from(6) shouldBe Digit.SIX
        Digit.from(7) shouldBe Digit.SEVEN
        Digit.from(8) shouldBe Digit.EIGHT
        Digit.from(9) shouldBe Digit.NINE
    }
})
