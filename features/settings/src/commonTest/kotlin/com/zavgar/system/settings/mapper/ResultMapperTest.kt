package com.zavgar.system.settings.mapper

import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.settings.model.LogoutResult
import com.zavgar.system.utils.result.AppResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsResultMapperTest {

    @Test
    fun `toLogoutResult maps Success without invoking the error mapper`() {
        var called = false
        val source: AppResult<Unit, LogoutError> = AppResult.Success(Unit)

        val result = source.toLogoutResult {
            called = true
            it.asSnackBarMessage()
        }

        assertEquals(LogoutResult.Success, result)
        assertTrue(!called)
    }

    @Test
    fun `toLogoutResult maps Error through the error mapper`() {
        val source: AppResult<Unit, LogoutError> = AppResult.Error(LogoutError.ServerError)

        val result = source.toLogoutResult { it.asSnackBarMessage() }

        assertTrue(result is LogoutResult.Error)
        assertEquals(SnackBarType.ERROR, result.message.type)
    }

    @Test
    fun `asSnackBarMessage routes every LogoutError through the shared mapper`() {
        assertEquals(SnackBarType.ERROR, LogoutError.NetworkError.asSnackBarMessage().type)
        assertEquals(SnackBarType.WARNING, LogoutError.TooManyRequestError.asSnackBarMessage().type)
    }
}
