package com.zavgar.system.wallet.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.mapAppError
import com.zavgar.system.utils.result.AppError
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.wallet.model.BalanceResult

fun Balance.toPresentation() = this.balance

fun GetBalanceError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is AppError -> mapAppError(this)
}

fun AppResult<Balance, GetBalanceError>.toBalanceResult(): BalanceResult = when (this) {
    is AppResult.Success -> BalanceResult.Success(data.toPresentation())
    is AppResult.Error -> BalanceResult.Error(error.asSnackBarMessage())
}
