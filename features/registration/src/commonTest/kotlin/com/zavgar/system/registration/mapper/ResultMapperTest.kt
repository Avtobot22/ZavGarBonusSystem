package com.zavgar.system.registration.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.registration.model.RegisterResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_user_already_exists
import com.zavgar.system.utils.result.AppResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegistrationResultMapperTest {

    @Test
    fun `toRegisterResult maps Success without invoking the error mapper`() {
        var called = false
        val source: AppResult<Unit, RegisterError> = AppResult.Success(Unit)

        val result = source.toRegisterResult {
            called = true
            it.asSnackBarMessage()
        }

        assertEquals(RegisterResult.Success, result)
        assertTrue(!called)
    }

    @Test
    fun `toRegisterResult maps Error through the error mapper`() {
        val source: AppResult<Unit, RegisterError> = AppResult.Error(RegisterError.UserAlreadyExists)

        val result = source.toRegisterResult { it.asSnackBarMessage() }

        assertTrue(result is RegisterResult.Error)
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_user_already_exists)),
            result.message,
        )
    }

    @Test
    fun `asSnackBarMessage maps domain-specific errors to warnings`() {
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_user_already_exists)),
            RegisterError.UserAlreadyExists.asSnackBarMessage(),
        )
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_format)),
            RegisterError.InvalidFormat.asSnackBarMessage(),
        )
    }

    @Test
    fun `asSnackBarMessage routes AppError-typed errors through the shared mapper`() {
        assertEquals(SnackBarType.ERROR, RegisterError.NetworkError.asSnackBarMessage().type)
        assertEquals(SnackBarType.ERROR, RegisterError.ServerError.asSnackBarMessage().type)
        assertEquals(SnackBarType.WARNING, RegisterError.TooManyRequestError.asSnackBarMessage().type)
    }
}
