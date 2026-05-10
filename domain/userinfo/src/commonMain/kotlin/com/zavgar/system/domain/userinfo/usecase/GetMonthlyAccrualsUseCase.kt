package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.model.AccrualsSumRequest
import com.zavgar.system.network.remote.LoyaltyService
import kotlinx.coroutines.withContext

class GetMonthlyAccrualsUseCase(
    private val loyaltyService: LoyaltyService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): Result<Int> =
        withContext(dispatcherProvider.io) {
            loyaltyService.getAccrualsSum(AccrualsSumRequest())
                .map { it.sum }
        }
}
