package de.tillhub.inputengine.data

import de.tillhub.inputengine.helper.EUR
import de.tillhub.inputengine.helper.eur
import de.tillhub.inputengine.helper.jpy
import de.tillhub.inputengine.helper.usd
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.math.BigInteger

class MoneyTest : DescribeSpec({

    describe("Money is zero") {
        it("should check correctly") {
            Money.zero(EUR).isZero().shouldBeTrue()
            Money.zero(EUR).isNotZero().shouldBeFalse()

            Money.from(BigInteger.ZERO, EUR).isZero().shouldBeTrue()
            Money.from(BigInteger.ZERO, EUR).isNotZero().shouldBeFalse()
        }
    }
    describe("isNegative") {
        it("should check correctly") {
            (-10).toBigInteger().eur.isNegative().shouldBeTrue()
            10.toBigInteger().eur.isNegative().shouldBeFalse()

            (-10.0).toBigDecimal().eur.isNegative().shouldBeTrue()
            10.0.toBigDecimal().eur.isNegative().shouldBeFalse()
        }
    }
    describe("Money is positive") {
        it("should check correctly") {
            (-10).toBigInteger().eur.isPositive().shouldBeFalse()
            10.toBigInteger().eur.isPositive().shouldBeTrue()
            0.toBigInteger().eur.isPositive(true).shouldBeTrue()

            (-10.0).toBigDecimal().eur.isPositive().shouldBeFalse()
            10.0.toBigDecimal().eur.isPositive().shouldBeTrue()
            0.toBigDecimal().eur.isPositive().shouldBeFalse()
        }
    }
    describe("plus operator") {
        it("should not work on different currencies") {
            shouldThrowExactly<IllegalArgumentException> { 1.0.toBigDecimal().eur + 1.0.toBigDecimal().usd }
            shouldThrowExactly<IllegalArgumentException> { 100.toBigInteger().eur + 100.toBigInteger().usd }
        }

        it("should add correctly") {
            1.00.toBigDecimal().eur + 0.00.toBigDecimal().eur shouldBe 1.00.toBigDecimal().eur
            (-1.00).toBigDecimal().eur + 10.00.toBigDecimal().eur shouldBe 9.0.toBigDecimal().eur
            1.00.toBigDecimal().eur + 12.34.toBigDecimal().eur shouldBe 1334.toBigInteger().eur
        }
    }
    describe("minus operator") {
        it("should not work on different currencies") {
            shouldThrowExactly<IllegalArgumentException> { 1.0.toBigDecimal().eur - 1.0.toBigDecimal().usd }
            shouldThrowExactly<IllegalArgumentException> { 1.0.toBigDecimal().usd - 1.0.toBigDecimal().eur }
        }

        it("should subtract correctly") {
            1.23456789.toBigDecimal().eur - 1.00.toBigDecimal().eur shouldBe 0.23456789.toBigDecimal().eur
            (-1.00).toBigDecimal().eur - 10.00.toBigDecimal().eur shouldBe (-11.0).toBigDecimal().eur
            1.00.toBigDecimal().eur - 0.00.toBigDecimal().eur shouldBe 1.00.toBigDecimal().eur
        }
    }
    describe("Money is isValid") {
        it("should check correctly") {
            (-10).toBigInteger().eur.isValid().shouldBeFalse()
            10.toBigInteger().eur.isValid().shouldBeTrue()
            1000000001.toBigInteger().eur.isValid().shouldBeFalse()

            (-10.0).toBigDecimal().eur.isValid().shouldBeFalse()
            10.0.toBigDecimal().eur.isValid().shouldBeTrue()
            10000001.toBigDecimal().eur.isValid().shouldBeFalse()
        }
    }
    describe("Money append") {
        it("should check correctly") {
            Money.append(1.toBigInteger().eur, Digit.ONE) shouldBe 0.11.toBigDecimal().eur
            Money.append((-1).toBigInteger().eur, Digit.ONE) shouldBe (-0.01).toBigDecimal().eur
            Money.append(1.toBigDecimal().eur, Digit.ONE) shouldBe 10.01.toBigDecimal().eur
            Money.append(
                1000000001.toBigInteger().eur,
                Digit.ONE
            ) shouldBe 1000000001.toBigInteger().eur
        }
    }
    describe("compareTo") {
        it("should throw exception") {
            shouldThrow<IllegalArgumentException> {
                10.toBigInteger().eur.compareTo(10.toBigInteger().jpy)
            }
            shouldThrow<IllegalArgumentException> {
                10.0.toBigDecimal().eur.compareTo(10.toBigDecimal().jpy)
            }
        }

        it("should be less") {
            ((-10).toBigInteger().eur < (-9).toBigInteger().eur).shouldBeTrue()
            (9.toBigInteger().eur < 10.toBigInteger().eur).shouldBeTrue()

            ((-10.00).toBigDecimal().eur < (-9.00).toBigDecimal().eur).shouldBeTrue()
            (10.00.toBigDecimal().eur < 10.01.toBigDecimal().eur).shouldBeTrue()
            (1.23456788.toBigDecimal().eur < 1.23456789.toBigDecimal().eur).shouldBeTrue()
        }

        it("should be equal") {
            ((-10).toBigInteger().eur == (-10).toBigInteger().eur).shouldBeTrue()
            (10.toBigInteger().eur == 10.toBigInteger().eur).shouldBeTrue()

            ((-10.00).toBigDecimal().eur == (-10.00).toBigDecimal().eur).shouldBeTrue()
            (10.00.toBigDecimal().eur == 10.00.toBigDecimal().eur).shouldBeTrue()
            (1.23456789.toBigDecimal().eur == 1.23456789.toBigDecimal().eur).shouldBeTrue()
        }

        it("should be greater") {
            ((-10).toBigInteger().eur < (-9).toBigInteger().eur).shouldBeTrue()
            (9.toBigInteger().eur < 10.toBigInteger().eur).shouldBeTrue()

            ((-9.00).toBigDecimal().eur > (-10.00).toBigDecimal().eur).shouldBeTrue()
            (10.01.toBigDecimal().eur > 10.00.toBigDecimal().eur).shouldBeTrue()
            (1.23456789.toBigDecimal().eur > 1.23456788.toBigDecimal().eur).shouldBeTrue()
        }
    }
    describe("removeLastMinorDigit") {
        it("should remove last minor digit") {
            Money.removeLastDigit(0.01.toBigDecimal().eur) shouldBe 0.toBigInteger().eur
            Money.removeLastDigit(11.toBigInteger().eur) shouldBe 1.toBigInteger().eur
        }
    }
})
