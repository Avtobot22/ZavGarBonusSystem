package com.zavgar.system.authorization.mapper

import com.zavgar.system.authorization.model.LoginResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_login
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.utils.result.AppResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthorizationResultMapperTest {

    @Test
    fun `toLoginResult maps Success to LoginResult Success without calling the error mapper`() {
        var mapperCalled = false
        val source: AppResult<Unit, AuthError> = AppResult.Success(Unit)

        val result = source.toLoginResult {
            mapperCalled = true
            it.asSnackBarMessage()
        }

        assertEquals(LoginResult.Success, result)
        assertTrue(!mapperCalled)
    }

    @Test
    fun `toLoginResult maps Error through the provided error mapper`() {
        val source: AppResult<Unit, AuthError> = AppResult.Error(AuthError.UserNotFound)

        val result = source.toLoginResult { it.asSnackBarMessage() }

        assertTrue(result is LoginResult.Error)
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_user_not_found)),
            result.message,
        )
    }

    @Test
    fun `asSnackBarMessage maps ValidationError to an invalid-login warning`() {
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_login)),
            AuthError.ValidationError.asSnackBarMessage(),
        )
    }

    @Test
    fun `asSnackBarMessage maps UserNotFound to a warning`() {
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_user_not_found)),
            AuthError.UserNotFound.asSnackBarMessage(),
        )
    }

    @Test
    fun `asSnackBarMessage routes AppError-typed errors through the shared mapper`() {
        assertEquals(SnackBarType.ERROR, AuthError.NetworkError.asSnackBarMessage().type)
        assertEquals(SnackBarType.ERROR, AuthError.ServerError.asSnackBarMessage().type)
        assertEquals(SnackBarType.WARNING, AuthError.TooManyRequestError().asSnackBarMessage().type)
        assertEquals(SnackBarType.ERROR, AuthError.UnknownError("x").asSnackBarMessage().type)
    }
}
