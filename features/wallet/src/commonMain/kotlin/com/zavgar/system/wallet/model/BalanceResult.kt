package com.zavgar.system.wallet.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface BalanceResult {
    data class Success(val balance: Int) : BalanceResult
    data class Error(val message: SnackBarMessage) : BalanceResult
}
