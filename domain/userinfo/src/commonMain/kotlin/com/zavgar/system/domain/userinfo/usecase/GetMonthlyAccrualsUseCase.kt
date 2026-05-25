package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.MonthlyAccrualsError
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult

class GetMonthlyAccrualsUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(): AppResult<Int, MonthlyAccrualsError> =
        profileRepository.getMonthlyAccruals()
}
