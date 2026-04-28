package com.zavgar.system.events

sealed interface AppEvent {
    data object Logout : AppEvent
}
