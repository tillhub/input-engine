package de.tillhub.inputengine.helper

import de.tillhub.inputengine.data.Digit
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

internal class NumberInputController(
    private val maxMajorDigits: Int = 8,
    private val maxMinorDigits: Int = 2
) {
    private var switchToMinorDigits = false
    private var switchToNegate = false
    private val _majorDigits: MutableList<Digit> = mutableListOf(Digit.ZERO)
    private val _minorDigits: MutableList<Digit> = mutableListOf()

    internal val majorDigits: List<Digit> get() = _majorDigits
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
                _majorDigits.addAll(BigIntegers.digits(number.toBigInteger()))
            }

            is BigInteger -> {
                _majorDigits.addAll(BigIntegers.digits(number))
            }

            is BigDecimal -> {
                val (major, minor) = number.divideAndRemainder(BigDecimal.ONE)
                _majorDigits.addAll(BigIntegers.digits(major.toBigInteger()))
                val minorValue = minor.setScale(maxMinorDigits, RoundingMode.HALF_UP)
                    .movePointRight(maxMinorDigits)
                    .toBigInteger()
                _minorDigits.addAll(BigIntegers.minorDigits(minorValue, maxMinorDigits))
            }

            is Double -> {
                val (major, minor) = number.toBigDecimal().divideAndRemainder(BigDecimal.ONE)
                _majorDigits.addAll(BigIntegers.digits(major.toBigInteger()))
                val minorValue = minor.setScale(maxMinorDigits, RoundingMode.HALF_UP)
                    .movePointRight(maxMinorDigits)
                    .toBigInteger()
                _minorDigits.addAll(BigIntegers.minorDigits(minorValue, maxMinorDigits))
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
            _majorDigits.fold(BigInteger.ZERO) { acc, digit ->
                acc.multiply(BigInteger.TEN).add(digit.value.toBigInteger())
            }.let { if (switchToNegate) it.negate() else it }
        } else {
            (_majorDigits + _minorDigits).fold(BigDecimal.ZERO) { acc, digit ->
                acc.multiply(BigDecimal.TEN)
                    .add(digit.value.toBigDecimal().movePointLeft(_minorDigits.size))
            }.let { if (switchToNegate) it.negate() else it }
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