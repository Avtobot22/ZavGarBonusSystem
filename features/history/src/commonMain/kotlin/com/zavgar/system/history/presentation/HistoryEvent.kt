package com.zavgar.system.history.presentation

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface HistoryEvent {
    data class ShowSnackbar(val message: SnackBarMessage) : HistoryEvent

    data object Logout : HistoryEvent
}
