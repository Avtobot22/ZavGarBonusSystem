package com.zavgar.system.history.model

import kotlinx.datetime.LocalDate

data class TransactionsRequest(
    val periodStart: LocalDate,
    val periodEnd: LocalDate,
    val cursor: String?,
    val limit: Int = 20,
)