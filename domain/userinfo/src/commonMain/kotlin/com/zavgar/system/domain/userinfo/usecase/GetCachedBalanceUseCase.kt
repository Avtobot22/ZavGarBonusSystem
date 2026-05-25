package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.model.CachedBalance
import com.zavgar.system.domain.userinfo.repository.ProfileRepository

class GetCachedBalanceUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(): CachedBalance? =
        profileRepository.getCachedBalance()
}
