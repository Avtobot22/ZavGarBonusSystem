package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.repository.UserProfileRepository

class DeleteProfileUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke() = userProfileRepository.delete()
}