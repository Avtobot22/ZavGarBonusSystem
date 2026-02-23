package com.zavgar.system.navigation.controller


import com.zavgar.system.coroutines.AppCoroutineScope
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.event.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

internal class NavEventControllerImpl(
    private val appCoroutineScope: AppCoroutineScope,
) : NavEventController {

    private val _eventState: MutableSharedFlow<Event> = MutableSharedFlow()

    override val eventState: SharedFlow<Event> =
        _eventState.shareIn(
            scope = CoroutineScope(appCoroutineScope.context),
            started = SharingStarted.WhileSubscribed(),
        )

    override fun sendEvent(event: Event) {
        appCoroutineScope.launch {
            _eventState.emit(event)
        }
    }
}
