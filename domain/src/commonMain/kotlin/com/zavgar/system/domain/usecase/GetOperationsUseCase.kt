package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.model.request.TransactionsRequest
import com.zavgar.system.domain.repository.LoyaltyRepository

class GetOperationsUseCase(
    private val loyaltyRepository: LoyaltyRepository
) {
    suspend operator fun invoke(transactionsRequest: TransactionsRequest) =
        loyaltyRepository.getOperations(transactionsRequest)
}