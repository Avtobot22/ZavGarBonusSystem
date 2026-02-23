package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.model.request.ChangePasswordRequest
import com.zavgar.system.domain.repository.UserProfileRepository

class ChangePasswordUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(changePasswordRequest: ChangePasswordRequest) =
        userProfileRepository.changePassword(changePasswordRequest)
}