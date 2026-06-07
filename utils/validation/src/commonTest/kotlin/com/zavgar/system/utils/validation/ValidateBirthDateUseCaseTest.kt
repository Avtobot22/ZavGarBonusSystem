package com.zavgar.system.utils.validation

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidateBirthDateUseCaseTest {

    private val validate = ValidateBirthDateUseCase()

    @Test
    fun `returns Valid for a non-null birth date`() {
        val result = validate(LocalDate(1990, 1, 1))

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `returns BLANK for a null birth date`() {
        val result = validate(null)

        assertEquals(ValidationResult.Invalid(BirthDateValidationError.BLANK), result)
    }
}
