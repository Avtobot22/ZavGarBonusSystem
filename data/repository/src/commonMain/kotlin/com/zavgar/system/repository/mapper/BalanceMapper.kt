package com.zavgar.system.repository.mapper

import com.zavgar.system.domain.model.response.Balance as DomainBalance
import com.zavgar.system.repository.model.response.BalanceResponse as RepoBalanceResponse


fun RepoBalanceResponse.toDomain() = DomainBalance(
    balance = balance,
)