package com.zavgar.system.confirmation.mapper

import com.zavgar.system.confirmation.model.ConfirmationResult
import com.zavgar.system.confirmation.model.ResendConfirmationResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_code
import com.zavgar.system.resources.error_invalid_phone
import com.zavgar.system.utils.result.AppResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfirmationResultMapperTest {

    @Test
    fun `toConfirmationResult maps Success and Error branches`() {
        val success: AppResult<Unit, ConfirmationError> = AppResult.Success(Unit)
        assertEquals(ConfirmationResult.Success, success.toConfirmationResult { it.asSnackBarMessage() })

        val error: AppResult<Unit, ConfirmationError> = AppResult.Error(ConfirmationError.InvalidCodeError)
        val mapped = error.toConfirmationResult { it.asSnackBarMessage() }
        assertTrue(mapped is ConfirmationResult.Error)
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_code)),
            mapped.message,
        )
    }

    @Test
    fun `confirmation asSnackBarMessage maps invalid code and AppError errors`() {
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_code)),
            ConfirmationError.InvalidCodeError.asSnackBarMessage(),
        )
        assertEquals(SnackBarType.ERROR, ConfirmationError.NetworkError.asSnackBarMessage().type)
        assertEquals(SnackBarType.WARNING, ConfirmationError.TooManyRequestError.asSnackBarMessage().type)
    }

    @Test
    fun `toResendConfirmationResult maps Success and Error branches`() {
        val success: AppResult<Unit, ResendConfirmationError> = AppResult.Success(Unit)
        assertEquals(
            ResendConfirmationResult.Success,
            success.toResendConfirmationResult { it.asSnackBarMessage() },
        )

        val error: AppResult<Unit, ResendConfirmationError> =
            AppResult.Error(ResendConfirmationError.InvalidPhone)
        val mapped = error.toResendConfirmationResult { it.asSnackBarMessage() }
        assertTrue(mapped is ResendConfirmationResult.Error)
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_phone)),
            mapped.message,
        )
    }

    @Test
    fun `resend asSnackBarMessage maps invalid phone and AppError errors`() {
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_phone)),
            ResendConfirmationError.InvalidPhone.asSnackBarMessage(),
        )
        assertEquals(SnackBarType.ERROR, ResendConfirmationError.ServerError.asSnackBarMessage().type)
    }
}
