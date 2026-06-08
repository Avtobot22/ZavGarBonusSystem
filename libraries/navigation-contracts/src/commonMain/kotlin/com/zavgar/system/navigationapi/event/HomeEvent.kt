package com.zavgar.system.navigationapi.event

import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.marker.TopLevel
import kotlinx.serialization.Serializable

/**
 * Event to navigate in bottomNavigation.
 */
object HomeEvent {

    @Serializable
    data class OnTabClick(val topLevel: TopLevel) : Event {
        override fun nextDestination(): Destination = topLevel as Destination
    }

    data object ToLogin : Event, ClearAndNavigate {
        override fun nextDestination(): Destination = AuthDestination.Login
    }
}
