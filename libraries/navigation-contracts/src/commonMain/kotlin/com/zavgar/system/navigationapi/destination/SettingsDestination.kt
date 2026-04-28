package com.zavgar.system.navigationapi.destination

import com.zavgar.system.navigationapi.marker.BottomBarVisible
import com.zavgar.system.parcelable.CommonParcelize
import kotlinx.serialization.Serializable

object SettingsDestination {

    @Serializable
    @CommonParcelize
    data object Profile : Destination, BottomBarVisible

}