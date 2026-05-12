package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class GetUserBalanceUseCase(
    private val profileRepository: ProfileRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<Balance, GetBalanceError> =
        withContext(dispatcherProvider.io) {
            profileRepository.getBalance()
        }
}
