package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.repository.UserProfileRepository

class GetProfileUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke() = userProfileRepository.getProfile()
}