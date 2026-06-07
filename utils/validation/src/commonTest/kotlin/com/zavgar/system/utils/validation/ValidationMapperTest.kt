package com.zavgar.system.utils.validation

import kotlin.test.Test
import kotlin.test.assertEquals

private enum class Domain { A, B }
private enum class Presentation { X, Y }

class ValidationMapperTest {

    @Test
    fun `toPresentation keeps Valid as Valid without invoking the mapper`() {
        var mapperCalled = false
        val source: ValidationResult<Domain> = ValidationResult.Valid

        val result = source.toPresentation {
            mapperCalled = true
            Presentation.X
        }

        assertEquals(ValidationResult.Valid, result)
        assertEquals(false, mapperCalled)
    }

    @Test
    fun `toPresentation maps the error of an Invalid result`() {
        val source: ValidationResult<Domain> = ValidationResult.Invalid(Domain.A)

        val result = source.toPresentation { domain ->
            when (domain) {
                Domain.A -> Presentation.X
                Domain.B -> Presentation.Y
            }
        }

        assertEquals(ValidationResult.Invalid(Presentation.X), result)
    }
}
