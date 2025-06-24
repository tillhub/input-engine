package de.tillhub.inputengine.helper

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import de.tillhub.inputengine.financial.data.Digit
import de.tillhub.inputengine.financial.helper.DigitBuilder

class NumberInputController(
    private val maxMajorDigits: Int = 8,
    private val maxMinorDigits: Int = 4
) {
    private var switchToMinorDigits = false
    private var switchToNegate = false
    private val _majorDigits: MutableList<Digit> = mutableListOf(Digit.ZERO)
    private val _minorDigits: MutableList<Digit> = mutableListOf()

    internal val minorDigits: List<Digit> get() = _minorDigits

    internal fun setValue(majorDigits: List<Digit>, minorDigits: List<Digit>, isNegative: Boolean) {
        this.switchToMinorDigits = minorDigits.isNotEmpty()
        this.switchToNegate = isNegative
        this._majorDigits.clear()
        this._majorDigits.addAll(majorDigits)
        this._minorDigits.clear()
        this._minorDigits.addAll(minorDigits)
    }

    fun setValue(number: Number) {
        _majorDigits.clear()
        _minorDigits.clear()
        when (number) {
            is Int -> {
                _majorDigits.addAll(DigitBuilder.digits(number.toBigInteger()))
            }

            is Double -> {
                val (major, minor) = number.toBigDecimal().divideAndRemainder(BigDecimal.ONE)
                _majorDigits.addAll(DigitBuilder.digits(major.toBigInteger()))
                val minorValue = minor.roundToDigitPositionAfterDecimalPoint(
                    digitPosition = maxMinorDigits.toLong(),
                    roundingMode = RoundingMode.AWAY_FROM_ZERO
                ).toBigInteger()
                _minorDigits.addAll(DigitBuilder.minorDigits(minorValue, maxMinorDigits))
            }
        }
        switchToMinorDigits = _minorDigits.isNotEmpty()
        switchToNegate = number.toDouble() < 0.0
    }

    internal fun addDigit(digit: Digit) {
        if (switchToMinorDigits) {
            if (_minorDigits.size < maxMinorDigits) {
                _minorDigits.add(digit)
            }
        } else {
            when {
                _majorDigits == listOf(Digit.ZERO) -> {
                    _majorDigits.clear()
                    _majorDigits.add(digit)
                }

                _majorDigits.size < maxMajorDigits -> {
                    _majorDigits.add(digit)
                }
            }
        }
    }

    fun deleteLast() {
        when {
            _minorDigits.isNotEmpty() -> _minorDigits.removeLast().also {
                switchToMinorDigits = _minorDigits.isNotEmpty()
            }

            _majorDigits.isNotEmpty() -> _majorDigits.removeLast()
        }
    }

    fun switchToMinor(switch: Boolean) {
        this.switchToMinorDigits = switch
        if (!switch) {
            _minorDigits.clear()
        }
    }

    fun switchNegate() {
        switchToNegate = !switchToNegate
    }

    fun value(): Number {
        return if (_minorDigits.isEmpty()) {
            val major = _majorDigits.fold(BigInteger.ZERO) { acc, digit ->
                acc * BigInteger.TEN +
                        BigInteger.fromInt(digit.value)
            }.let { if (switchToNegate) it.negate() else it }

            major.longValue(false)
        } else {
            val totalDigits = _majorDigits + _minorDigits
            val scale = _minorDigits.size

            val decimal = totalDigits.fold(BigDecimal.ZERO) { acc, digit ->
                acc * BigDecimal.TEN +
                        BigDecimal.fromInt(digit.value)
            }.let {
                val divisor = BigDecimal.TEN.pow(scale)
                val scaled = it.divide(divisor, com.ionspin.kotlin.bignum.decimal.DecimalMode(34))
                if (switchToNegate) scaled.negate() else scaled
            }

            decimal.doubleValue(false)
        }
    }


    fun clear() {
        _majorDigits.clear()
        _majorDigits.add(Digit.ZERO)
        _minorDigits.clear()
        switchToMinorDigits = false
        switchToNegate = false
    }
}