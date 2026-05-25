package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult

class DeleteUserProfileUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(): AppResult<Unit, DeleteError> =
        profileRepository.delete()
}
