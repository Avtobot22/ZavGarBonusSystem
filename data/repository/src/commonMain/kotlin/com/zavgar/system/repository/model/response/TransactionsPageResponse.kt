package com.zavgar.system.repository.model.response

data class TransactionsPageResponse(
    val transactions: List<Transaction>,
    val newCursor: String? = null,
    val hasMore: Boolean
)
