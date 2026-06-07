package com.zavgar.system.utils.validation

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidatePhoneUseCaseTest {

    private val validate = ValidatePhoneUseCase()

    @Test
    fun `returns Valid for a 10-digit phone`() {
        val result = validate("1234567890")

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns BLANK for an empty phone`() {
        val result = validate("")

        assertEquals(ValidationResult.Invalid(PhoneValidationError.BLANK), result)
    }

    @Test
    fun `returns BLANK for a whitespace-only phone`() {
        val result = validate("   ")

        assertEquals(ValidationResult.Invalid(PhoneValidationError.BLANK), result)
    }

    @Test
    fun `returns INVALID_FORMAT when phone contains non-digits`() {
        val result = validate("12345678ab")

        assertEquals(ValidationResult.Invalid(PhoneValidationError.INVALID_FORMAT), result)
    }

    @Test
    fun `returns INVALID_LENGTH for a too short digit string`() {
        val result = validate("12345")

        assertEquals(ValidationResult.Invalid(PhoneValidationError.INVALID_LENGTH), result)
    }

    @Test
    fun `returns INVALID_LENGTH for a too long digit string`() {
        val result = validate("123456789012")

        assertEquals(ValidationResult.Invalid(PhoneValidationError.INVALID_LENGTH), result)
    }

    @Test
    fun `format failure wins over length when both are violated`() {
        // 10 chars but contains a letter: digitsOnly is checked before exactLength
        val result = validate("123456789x")

        assertEquals(ValidationResult.Invalid(PhoneValidationError.INVALID_FORMAT), result)
    }
}
