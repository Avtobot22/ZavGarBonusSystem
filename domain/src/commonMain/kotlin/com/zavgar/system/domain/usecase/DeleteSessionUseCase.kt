package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.repository.DataSourceRepository

class DeleteSessionUseCase(
    private val dataSourceRepository: DataSourceRepository,
) {
    suspend operator fun invoke() = dataSourceRepository.deleteSession()
}