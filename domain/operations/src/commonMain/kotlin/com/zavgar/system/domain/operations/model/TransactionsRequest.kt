package com.zavgar.system.domain.operations.model

import kotlinx.datetime.LocalDate

private const val DEFAULT_PAGE_LIMIT = 20

data class TransactionsRequest(
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val cursor: String?,
    val limit: Int = DEFAULT_PAGE_LIMIT,
    val sortOrder: SortOrder = SortOrder.DESC,
)
