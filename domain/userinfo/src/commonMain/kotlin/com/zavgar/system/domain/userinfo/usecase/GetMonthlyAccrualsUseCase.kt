package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.MonthlyAccrualsError
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class GetMonthlyAccrualsUseCase(
    private val profileRepository: ProfileRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<Int, MonthlyAccrualsError> =
        withContext(dispatcherProvider.io) {
            profileRepository.getMonthlyAccruals()
        }
}
