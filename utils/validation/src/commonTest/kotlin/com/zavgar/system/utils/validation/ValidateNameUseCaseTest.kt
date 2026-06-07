package com.zavgar.system.utils.validation

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidateNameUseCaseTest {

    private val validate = ValidateNameUseCase()

    @Test
    fun `returns Valid for a non-blank name`() {
        val result = validate("John")

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns BLANK for an empty name`() {
        val result = validate("")

        assertEquals(ValidationResult.Invalid(NameValidationError.BLANK), result)
    }

    @Test
    fun `returns BLANK for a whitespace-only name`() {
        val result = validate("   ")

        assertEquals(ValidationResult.Invalid(NameValidationError.BLANK), result)
    }
}
