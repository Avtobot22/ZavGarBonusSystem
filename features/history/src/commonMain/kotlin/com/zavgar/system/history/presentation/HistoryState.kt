package com.zavgar.system.history.presentation

import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.history.model.DatePickerType
import com.zavgar.system.history.model.History
import kotlinx.datetime.LocalDate

data class HistoryState(
    val screenState: ScreenState = ScreenState.Initial,

    val periodStart: LocalDate,
    val periodStartText: String = periodStart.toDisplayString(),
    val periodEnd: LocalDate,
    val periodEndText: String = periodEnd.toDisplayString(),

    val history: History = History.EMPTY,

    val isLoadingNextPage: Boolean = false,
    val isRefreshing: Boolean = false,

    val datePickerOpen: DatePickerType? = null

) {

    sealed interface ScreenState {
        data object Initial : ScreenState
        data object Loading : ScreenState
        data object Reloading : ScreenState
        data object Content : ScreenState
        data object Error : ScreenState
    }
}
