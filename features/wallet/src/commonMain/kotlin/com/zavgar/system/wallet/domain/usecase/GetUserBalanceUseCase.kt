package com.zavgar.system.wallet.domain.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.remote.LoyaltyService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import com.zavgar.system.wallet.domain.error.GetBalanceError
import com.zavgar.system.wallet.domain.error.toGetBalanceError
import com.zavgar.system.wallet.domain.model.Balance
import kotlinx.coroutines.withContext

class GetUserBalanceUseCase(
    private val loyaltyService: LoyaltyService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<Balance, GetBalanceError> =
        withContext(dispatcherProvider.io) {
            loyaltyService.getBalance()
                .map { Balance(balance = it.balance) }
                .toAppResult(Throwable::toGetBalanceError)
        }
}
