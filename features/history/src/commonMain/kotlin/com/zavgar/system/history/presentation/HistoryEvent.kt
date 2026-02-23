package com.zavgar.system.history.presentation

import com.zavgar.system.core.presentation.util.UiText

sealed interface HistoryEvent {
    data class ShowSnackbar(val message: UiText) : HistoryEvent

    data object Logout : HistoryEvent
}