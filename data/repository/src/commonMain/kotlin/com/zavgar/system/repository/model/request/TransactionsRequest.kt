package com.zavgar.system.repository.model.request

import kotlinx.datetime.LocalDate

data class TransactionsRequest(
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val cursor: String?,
    val limit: Int,
    val sortOrder: SortOrder
)
