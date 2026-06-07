package com.zavgar.system.utils.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private enum class TestError { FIRST, SECOND, THIRD }

class ValidationScopeTest {

    @Test
    fun `validate with no rules is Valid`() {
        val result = validate<TestError> { }

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `validate with all passing rules is Valid`() {
        val result = validate<TestError> {
            check(TestError.FIRST) { true }
            check(TestError.SECOND) { true }
        }

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `first failing rule wins`() {
        val result = validate<TestError> {
            check(TestError.FIRST) { false }
            check(TestError.SECOND) { false }
        }

        assertEquals(ValidationResult.Invalid(TestError.FIRST), result)
    }

    @Test
    fun `failing rule after a passing one is reported`() {
        val result = validate<TestError> {
            check(TestError.FIRST) { true }
            check(TestError.SECOND) { false }
        }

        assertEquals(ValidationResult.Invalid(TestError.SECOND), result)
    }

    @Test
    fun `predicate is not evaluated once an error is recorded`() {
        var thirdEvaluated = false

        val result = validate<TestError> {
            check(TestError.FIRST) { false }
            check(TestError.THIRD) {
                thirdEvaluated = true
                false
            }
        }

        assertEquals(ValidationResult.Invalid(TestError.FIRST), result)
        assertTrue(!thirdEvaluated, "subsequent predicates must be short-circuited")
    }

    @Test
    fun `notBlank passes for non-blank and fails for blank`() {
        assertEquals(ValidationResult.Valid, validate<TestError> { notBlank("x", TestError.FIRST) })
        assertEquals(
            ValidationResult.Invalid(TestError.FIRST),
            validate<TestError> { notBlank("  ", TestError.FIRST) },
        )
    }

    @Test
    fun `digitsOnly passes for digits and fails otherwise`() {
        assertEquals(ValidationResult.Valid, validate<TestError> { digitsOnly("123", TestError.FIRST) })
        assertEquals(
            ValidationResult.Invalid(TestError.FIRST),
            validate<TestError> { digitsOnly("12a", TestError.FIRST) },
        )
    }

    @Test
    fun `digitsOnly treats empty string as valid`() {
        // String.all returns true for an empty sequence
        assertEquals(ValidationResult.Valid, validate<TestError> { digitsOnly("", TestError.FIRST) })
    }

    @Test
    fun `exactLength compares against the expected length`() {
        assertEquals(ValidationResult.Valid, validate<TestError> { exactLength("123", 3, TestError.FIRST) })
        assertEquals(
            ValidationResult.Invalid(TestError.FIRST),
            validate<TestError> { exactLength("12", 3, TestError.FIRST) },
        )
    }

    @Test
    fun `minLength passes at and above the boundary`() {
        assertEquals(ValidationResult.Valid, validate<TestError> { minLength("123", 3, TestError.FIRST) })
        assertEquals(ValidationResult.Valid, validate<TestError> { minLength("1234", 3, TestError.FIRST) })
        assertEquals(
            ValidationResult.Invalid(TestError.FIRST),
            validate<TestError> { minLength("12", 3, TestError.FIRST) },
        )
    }
}
