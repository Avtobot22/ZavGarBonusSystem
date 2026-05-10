package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.error.toGetBalanceError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.network.remote.LoyaltyService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
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
