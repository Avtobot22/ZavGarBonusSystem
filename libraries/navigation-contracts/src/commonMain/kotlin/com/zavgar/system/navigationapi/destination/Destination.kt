package com.zavgar.system.navigationapi.destination

import com.zavgar.system.navigationapi.marker.BottomBarVisible
import com.zavgar.system.navigationapi.marker.TopLevel
import com.zavgar.system.parcelable.CommonParcelable
import com.zavgar.system.parcelable.CommonParcelize
import kotlinx.serialization.Serializable

sealed interface Destination : CommonParcelable {

    @CommonParcelize
    @Serializable
    data object Back : Destination

    companion object {

        const val URI: String = "app://com.zavgar.system.app"
    }
}

/**
 * All top-level destinations.
 * Боттом навигация
 */
val TopLevelDestinations: Set<TopLevel> = setOf(
    HomeDestination.Wallet,
    HomeDestination.Settings,
    HomeDestination.History
)

/**
 * All destinations that should show the top app bar.
 */
val BottomBarVisibleDestinations = setOf<BottomBarVisible>(
    HomeDestination.Wallet,
    HomeDestination.Settings,
    HomeDestination.History,
)