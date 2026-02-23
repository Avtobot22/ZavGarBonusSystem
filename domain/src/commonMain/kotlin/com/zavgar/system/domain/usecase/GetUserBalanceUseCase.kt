package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.repository.LoyaltyRepository

class GetUserBalanceUseCase(
    private val loyaltyRepository: LoyaltyRepository
) {
    suspend operator fun invoke() = loyaltyRepository.getBalance()
}