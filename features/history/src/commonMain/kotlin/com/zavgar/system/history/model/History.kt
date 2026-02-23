package com.zavgar.system.history.model

data class History(
    val transactions: List<HistoryItem>,
    val nextCursor: String?,
    val hasMore: Boolean
)
