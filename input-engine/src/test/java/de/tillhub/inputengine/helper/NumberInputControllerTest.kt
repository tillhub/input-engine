package de.tillhub.inputengine.helper

import de.tillhub.inputengine.data.Digit
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NumberInputControllerTest : FunSpec({

    val target = NumberInputController()

    test("setValue") {
       target.setValue(listOf(Digit.ONE, Digit.TWO), emptyList())
       target.value() shouldBe 12.toBigInteger()

       target.clear()
       target.setValue(listOf(Digit.FIVE, Digit.SEVEN), listOf(Digit.NINE))
       target.value() shouldBe 57.9.toBigDecimal()
    }

    test("setValue: Number") {
        target.setValue(9876.toBigInteger())
        target.value() shouldBe 9876.toBigInteger()

        target.setValue(367.toBigDecimal())
        target.value() shouldBe 367.toBigInteger()

        target.setValue(567.126.toBigDecimal())
        target.value() shouldBe 567.13.toBigDecimal()
    }

    test("addDigit") {
      target.setValue(listOf(Digit.ONE, Digit.TWO), emptyList())
      target.addDigit(Digit.NINE)
      target.value() shouldBe 129.toBigInteger()
    }

    test("deleteLast") {
     target.setValue(listOf(Digit.ONE, Digit.TWO, Digit.NINE), listOf(Digit.EIGHT, Digit.SEVEN))
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
     target.setValue(listOf(Digit.FIVE, Digit.SIX), emptyList())
     target.value() shouldBe 56.toBigInteger()
     target.switchToMinor(true)
     target.addDigit(Digit.TWO)
     target.value() shouldBe 56.2.toBigDecimal()
    }

    test("clear") {
     target.setValue(listOf(Digit.ONE, Digit.TWO, Digit.NINE), listOf(Digit.EIGHT, Digit.SEVEN))
     target.clear()
     target.value() shouldBe 0.toBigInteger()
    }
})