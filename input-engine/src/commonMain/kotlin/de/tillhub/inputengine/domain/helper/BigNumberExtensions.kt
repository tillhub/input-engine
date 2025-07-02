package de.tillhub.inputengine.domain.helper

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

// BigInteger
internal fun pow10(exp: Int): BigInteger = BigInteger.TEN.pow(exp)
internal fun BigInteger.isPositive(includeZero: Boolean = false): Boolean {
    return if (includeZero) signum() >= 0 else signum() > 0
}
fun Number.toBigInteger(): BigInteger = when (this) {
    is Int -> this.toBigInteger()
    is Long -> this.toBigInteger()
    is Double -> this.toBigDecimal().toBigInteger()
    else -> throw IllegalArgumentException("Unsupported Number type: ${this::class}")
}

// BigDecimal
internal fun pow10decimal(exp: Int): BigDecimal = BigDecimal.TEN.pow(exp)
fun Number.toBigDecimal(): BigDecimal = when (this) {
    is Int -> this.toBigDecimal()
    is Long -> this.toBigDecimal()
    is Float -> this.toDouble().toBigDecimal() // avoid precision loss with Float
    is Double -> this.toBigDecimal()
    else -> throw IllegalArgumentException("Unsupported Number type: ${this::class}")
}
