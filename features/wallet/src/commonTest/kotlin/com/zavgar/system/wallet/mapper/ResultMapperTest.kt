package com.zavgar.system.wallet.mapper

import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.wallet.model.BalanceResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WalletResultMapperTest {

    @Test
    fun `toPresentation extracts the raw balance value`() {
        assertEquals(1234, Balance(1234).toPresentation())
    }

    @Test
    fun `toBalanceResult maps Success to the balance value`() {
        val source: AppResult<Balance, GetBalanceError> = AppResult.Success(Balance(500))

        val result = source.toBalanceResult()

        assertTrue(result is BalanceResult.Success)
        assertEquals(500, result.balance)
    }

    @Test
    fun `toBalanceResult maps Error through the shared AppError mapper`() {
        val source: AppResult<Balance, GetBalanceError> = AppResult.Error(GetBalanceError.NetworkError)

        val result = source.toBalanceResult()

        assertTrue(result is BalanceResult.Error)
        assertEquals(SnackBarType.ERROR, result.message.type)
    }

    @Test
    fun `asSnackBarMessage routes every GetBalanceError through the shared mapper`() {
        assertEquals(SnackBarType.ERROR, GetBalanceError.ServerError.asSnackBarMessage().type)
        assertEquals(SnackBarType.WARNING, GetBalanceError.TooManyRequestError().asSnackBarMessage().type)
        assertEquals(SnackBarType.ERROR, GetBalanceError.UnknownError("x").asSnackBarMessage().type)
    }
}
