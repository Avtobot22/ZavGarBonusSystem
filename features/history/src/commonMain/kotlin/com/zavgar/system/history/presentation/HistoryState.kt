package com.zavgar.system.history.presentation

import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.history.model.DatePickerType
import com.zavgar.system.history.model.HistoryItem
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

data class HistoryState(
    val periodStart: LocalDate = defaultStartDate,
    val periodStartText: String = defaultStartDate.toDisplayString(),
    val periodEnd: LocalDate = defaultEndDate,
    val periodEndText: String = defaultEndDate.toDisplayString(),

    val history: List<HistoryItem> = emptyList(),


    val isLoadingFirstPage: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val isRefreshing: Boolean = false,

    val nextCursor: String? = null,
    val hasMore: Boolean = true,

    val datePickerOpen: DatePickerType? = null

) {
    companion object {
        private val defaultEndDate: LocalDate
            get() = Clock.System.todayIn(TimeZone.currentSystemDefault())

        private val defaultStartDate: LocalDate
            get() = LocalDate(
                year = defaultEndDate.year,
                month = defaultEndDate.month,
                day = 1
            )
    }
}
