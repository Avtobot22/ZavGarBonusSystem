package com.zavgar.system.history.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.operations.error.OperationsError
import com.zavgar.system.domain.operations.model.TransactionsPageResponse
import com.zavgar.system.history.model.TransactionsResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.utils.result.AppResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistoryResultMapperTest {

    private val emptyPage = TransactionsPageResponse(emptyList(), newCursor = null, hasMore = false)

    @Test
    fun `toTransactionsResult maps Success to a Success carrying the page`() {
        val source: AppResult<TransactionsPageResponse, OperationsError> =
            AppResult.Success(emptyPage)

        val result = source.toTransactionsResult()

        assertTrue(result is TransactionsResult.Success)
        assertEquals(emptyPage, result.page)
    }

    @Test
    fun `toTransactionsResult maps Error to an Error carrying the snackbar`() {
        val source: AppResult<TransactionsPageResponse, OperationsError> =
            AppResult.Error(OperationsError.UserNotFound)

        val result = source.toTransactionsResult()

        assertTrue(result is TransactionsResult.Error)
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_user_not_found)),
            result.message,
        )
    }

    @Test
    fun `asSnackBarMessage maps ValidationError to a warning`() {
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_format)),
            OperationsError.ValidationError.asSnackBarMessage(),
        )
    }

    @Test
    fun `asSnackBarMessage maps UserNotFound to a warning`() {
        assertEquals(
            SnackBarMessage.warning(UiText.Resource(Res.string.error_user_not_found)),
            OperationsError.UserNotFound.asSnackBarMessage(),
        )
    }

    @Test
    fun `asSnackBarMessage maps AppError-typed errors via the shared mapper as errors`() {
        // NetworkError implements AppError.Network -> handled by the shared mapAppError branch.
        assertEquals(SnackBarType.ERROR, OperationsError.NetworkError.asSnackBarMessage().type)
        assertEquals(SnackBarType.ERROR, OperationsError.ServerError.asSnackBarMessage().type)
        assertEquals(SnackBarType.WARNING, OperationsError.TooManyRequestError.asSnackBarMessage().type)
    }
}
