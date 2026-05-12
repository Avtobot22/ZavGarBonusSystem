package com.zavgar.system.domain.session.usecase

import com.zavgar.system.domain.session.repository.SessionRepository

class GetSessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() = sessionRepository.getSession()
}
