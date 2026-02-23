package com.zavgar.system.navigationapi.destination

import com.zavgar.system.parcelable.CommonParcelize
import kotlinx.serialization.Serializable

object AuthDestination {

    @Serializable
    @CommonParcelize
    data object Login : Destination

    @Serializable
    @CommonParcelize
    data object Register : Destination

    @CommonParcelize
    @Serializable
    data class Confirmation(val phone: String, val isRegistration: Boolean) : Destination

    @Serializable
    @CommonParcelize
    data object Reset : Destination
}