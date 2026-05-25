package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult

class GetUserBalanceUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(): AppResult<Balance, GetBalanceError> =
        profileRepository.getBalance()
}
