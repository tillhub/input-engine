package de.tillhub.inputengine.helper

import de.tillhub.inputengine.data.Digit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NumberInputControllerTest : FunSpec({

    val target = NumberInputController()

    test("setValue") {
        target.setValue(listOf(Digit.ONE, Digit.TWO), emptyList(), true)
        target.value() shouldBe 12.toBigInteger().negate()

        target.clear()
        target.setValue(listOf(Digit.FIVE, Digit.SEVEN), listOf(Digit.NINE), false)
        target.value() shouldBe 57.9.toBigDecimal()
    }

    test("setValue: Number") {
        target.setValue(856)
        target.value() shouldBe 856.toBigInteger()

        target.setValue(9876.toBigInteger())
        target.value() shouldBe 9876.toBigInteger()

        target.setValue(567.126.toBigDecimal())
        target.value() shouldBe 567.126.toBigDecimal()

        target.setValue(567.1267.toBigDecimal())
        target.value() shouldBe 567.1267.toBigDecimal()

        target.setValue(567.12671.toBigDecimal())
        target.value() shouldBe 567.1267.toBigDecimal()

        target.setValue(367.12)
        target.value() shouldBe 367.12.toBigDecimal()
    }

    test("addDigit") {
        target.setValue(listOf(Digit.ONE, Digit.TWO), emptyList(), false)
        target.addDigit(Digit.NINE)
        target.value() shouldBe 129.toBigInteger()
    }

    test("deleteLast") {
        target.setValue(
            listOf(Digit.ONE, Digit.TWO, Digit.NINE),
            listOf(Digit.EIGHT, Digit.SEVEN),
            false
        )
        target.value() shouldBe 129.87.toBigDecimal()
        target.deleteLast()
        target.value() shouldBe 129.8.toBigDecimal()
        target.deleteLast()
        target.value() shouldBe 129.toBigInteger()
        target.deleteLast()
        target.value() shouldBe 12.toBigInteger()
        target.deleteLast()
        target.value() shouldBe 1.toBigInteger()
        target.deleteLast()
        target.value() shouldBe 0.toBigInteger()
    }

    test("switchToMinor") {
        target.setValue(listOf(Digit.FIVE, Digit.SIX), emptyList(), false)
        target.value() shouldBe 56.toBigInteger()
        target.switchToMinor(true)
        target.addDigit(Digit.TWO)
        target.value() shouldBe 56.2.toBigDecimal()
    }

    test("switchNegate") {
        target.setValue(5)
        target.switchNegate()
        target.value() shouldBe (-5).toBigInteger()
    }

    test("clear") {
        target.setValue(
            listOf(Digit.ONE, Digit.TWO, Digit.NINE),
            listOf(Digit.EIGHT, Digit.SEVEN),
            false
        )
        target.clear()
        target.value() shouldBe 0.toBigInteger()
    }
})
