package com.zavgar.system.navigationapi.controller

import com.zavgar.system.navigationapi.event.Event
import kotlinx.coroutines.flow.SharedFlow

/**
 * Controller responsible to handle the navigation events.
 */
interface NavEventController {

    /**
     * Flow to observe the navigation events.
     */
    val eventState: SharedFlow<Event>

    /**
     * Sends the event to the controller.
     *
     * @param event the event to be sent
     */
    fun sendEvent(event: Event)
}
