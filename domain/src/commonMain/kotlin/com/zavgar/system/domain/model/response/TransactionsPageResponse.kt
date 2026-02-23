package com.zavgar.system.domain.model.response

data class TransactionsPageResponse(
    val transactions: List<Transaction>,
    val newCursor: String? = null,
    val hasMore: Boolean
)
