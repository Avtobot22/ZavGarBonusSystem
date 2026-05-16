package com.zavgar.system.history.presentation

import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.history.model.DatePickerType
import com.zavgar.system.history.model.History
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

data class HistoryState(
    val screenState: ScreenState = ScreenState.Initial,

    val periodStart: LocalDate = defaultStartDate,
    val periodStartText: String = defaultStartDate.toDisplayString(),
    val periodEnd: LocalDate = defaultEndDate,
    val periodEndText: String = defaultEndDate.toDisplayString(),

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
