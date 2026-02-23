package com.zavgar.system.domain.model.request

import kotlinx.datetime.LocalDate

data class TransactionsRequest(
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val cursor: String?,
    val limit: Int = 20,
    val sortOrder: SortOrder = SortOrder.DESC
)
