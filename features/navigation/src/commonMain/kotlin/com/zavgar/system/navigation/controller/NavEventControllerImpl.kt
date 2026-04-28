package com.zavgar.system.navigation.controller

import com.zavgar.system.coroutines.AppCoroutineScope
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.event.Event
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class NavEventControllerImpl(
    private val appCoroutineScope: AppCoroutineScope,
) : NavEventController {

    private val _eventState = MutableSharedFlow<Event>(
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override val eventState: SharedFlow<Event> = _eventState.asSharedFlow()

    override fun sendEvent(event: Event) {
        appCoroutineScope.launch { _eventState.emit(event) }
    }
}
