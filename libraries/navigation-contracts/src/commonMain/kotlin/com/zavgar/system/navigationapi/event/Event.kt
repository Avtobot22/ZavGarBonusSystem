package com.zavgar.system.navigationapi.event

import com.zavgar.system.navigationapi.destination.Destination

sealed interface Event {

    fun nextDestination(): Destination

    data object OnBack : Event {
        override fun nextDestination(): Destination = Destination.Back
    }
}
