package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult

class LogoutUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(): AppResult<Unit, LogoutError> =
        profileRepository.logout()
}
