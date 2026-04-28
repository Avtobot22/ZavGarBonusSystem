package com.zavgar.system.events

import com.zavgar.system.coroutines.AppCoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface AppEventBus {
    val events: SharedFlow<AppEvent>
    fun emit(event: AppEvent)
}

internal class AppEventBusImpl(
    private val scope: AppCoroutineScope,
) : AppEventBus {

    private val _events = MutableSharedFlow<AppEvent>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val events: SharedFlow<AppEvent> = _events.asSharedFlow()

    override fun emit(event: AppEvent) {
        scope.launch { _events.emit(event) }
    }
}
