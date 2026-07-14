package com.zavgar.system.history.presentation

import com.zavgar.system.history.model.DatePickerType
import kotlinx.datetime.LocalDate

sealed interface HistoryIntent {
    data object ScreenEntered : HistoryIntent
    data class OpenDatePicker(val type: DatePickerType) : HistoryIntent
    data class CloseDatePicker(val type: DatePickerType, val date: LocalDate) : HistoryIntent
    data object DismissDatePicker : HistoryIntent
    data object Refresh : HistoryIntent

    data object LoadNextPage : HistoryIntent

    data object Retry : HistoryIntent
}
