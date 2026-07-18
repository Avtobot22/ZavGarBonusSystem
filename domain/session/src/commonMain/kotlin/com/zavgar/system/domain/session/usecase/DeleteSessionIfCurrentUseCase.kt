package com.zavgar.system.domain.session.usecase

import com.zavgar.system.domain.session.repository.SessionRepository

class DeleteSessionIfCurrentUseCase(
    private val sessionRepository: SessionRepository,
) {

    suspend operator fun invoke(refreshToken: String) =
        sessionRepository.deleteSessionIfRefreshTokenMatches(refreshToken)
}
