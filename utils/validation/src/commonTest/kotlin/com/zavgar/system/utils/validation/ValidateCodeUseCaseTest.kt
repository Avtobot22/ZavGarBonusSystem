package com.zavgar.system.utils.validation

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidateCodeUseCaseTest {

    private val validate = ValidateCodeUseCase()

    @Test
    fun `returns Valid for a code of exact required length`() {
        val result = validate("1234")

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns Valid for a code longer than required length`() {
        val result = validate("123456")

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns BLANK for an empty code`() {
        val result = validate("")

        assertEquals(ValidationResult.Invalid(CodeValidationError.BLANK), result)
    }

    @Test
    fun `returns TOO_SHORT for a code below required length`() {
        val result = validate("12")

        assertEquals(ValidationResult.Invalid(CodeValidationError.TOO_SHORT), result)
    }

    @Test
    fun `blank wins over too short when code is empty`() {
        // notBlank is evaluated before minLength
        val result = validate("")

        assertEquals(ValidationResult.Invalid(CodeValidationError.BLANK), result)
    }
}
