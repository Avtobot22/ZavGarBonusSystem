package com.zavgar.system.utils.validation

/**
 * Accumulates validation rules and keeps the first failing one.
 * Rules are declared via [check] (or the string helpers below); the first
 * failed rule wins, the rest are short-circuited.
 */
class ValidationScope<E> {

    private var firstError: E? = null

    fun check(error: E, isValid: () -> Boolean) {
        if (firstError == null && !isValid()) {
            firstError = error
        }
    }

    internal fun result(): ValidationResult<E> =
        firstError?.let { ValidationResult.Invalid(it) } ?: ValidationResult.Valid
}

/**
 * Entry point of the validation builder: declare the rules to check inside [block].
 */
fun <E> validate(block: ValidationScope<E>.() -> Unit): ValidationResult<E> =
    ValidationScope<E>().apply(block).result()

fun <E> ValidationScope<E>.notBlank(value: String, error: E) =
    check(error) { value.isNotBlank() }

fun <E> ValidationScope<E>.digitsOnly(value: String, error: E) =
    check(error) { value.all(Char::isDigit) }

fun <E> ValidationScope<E>.exactLength(value: String, length: Int, error: E) =
    check(error) { value.length == length }

fun <E> ValidationScope<E>.minLength(value: String, length: Int, error: E) =
    check(error) { value.length >= length }
